package com.example.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.example.myweather.gson.AirQualityBean;
import com.example.myweather.gson.FutureBean;
import com.example.myweather.gson.ResultBean;
import com.example.myweather.gson.Weather;
import com.example.myweather.util.HttpUtil;
import com.example.myweather.util.LogUtil;
import com.example.myweather.util.WeatherParseUtil;

import java.io.IOException;
import java.net.Inet4Address;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private TextView titleCity;
    private TextView nowUpdateTime;
    private TextView nowDegree;
    private TextView nowInfo;
    private LinearLayout forecastFuture;
    private TextView aqi;
    private TextView pm25;
    private TextView dressingIndex;
    private TextView coldIndex;
    private TextView exerciseIndex;
    private ImageView background;
    private Button titleNva;
    private Button titlePosition;
    public DrawerLayout drawerLayout;
    private String updateTime;
    public SwipeRefreshLayout refreshLayout;
    private String currentCounty;
    private static final String TAG = "WeatherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        findView();
        //读取更新时间
        readUpdateTime();
        //设置系统状态栏
        setStatusBar();
        //加载背景
        loadBackground();
        //加载天气信息
        loadWeatherInfo();
        //加载刷新事件
        loadRefreshEvent();
        //设置侧滑栏事件
        setNav();
        //自动定位
        autoPosition();
    }

    private void autoPosition() {

        titlePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationClient client = new LocationClient(WeatherActivity.this);
                LocationClientOption option = new LocationClientOption();
                option.setIsNeedAddress(true);
                client.setLocOption(option);
                client.registerLocationListener(new BDAbstractLocationListener() {
                    @Override
                    public void onReceiveLocation(BDLocation bdLocation) {
                        String county = null;
                        if(bdLocation.getDistrict().contains("县")){
                            county= bdLocation.getDistrict().split("县")[0];
                        }
                        if(bdLocation.getDistrict().contains("区")){
                            county= bdLocation.getDistrict().split("区")[0];
                        }
                            refreshLayout.setRefreshing(true);
                            Toast.makeText(WeatherActivity.this, "正在定位当前城市", Toast.LENGTH_SHORT).show();
                            LogUtil.d(TAG,"county:"+county);
                            requestWeather(county);
                            client.stop();
                    }
                });
                client.start();
            }
        });
    }

    private void setNav() {
        titleNva.setOnClickListener((view) -> drawerLayout.openDrawer(Gravity.START));
    }

    private void loadRefreshEvent() {
        refreshLayout.setColorSchemeColors(Color.RED);
        refreshLayout.setOnRefreshListener(() ->{
            requestWeather(currentCounty);
            requestImage();
        });
    }

    private void setStatusBar() {
        if(Build.VERSION.SDK_INT >= 21){
            //安卓5.0以上才能调整状态栏
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void loadWeatherInfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //获取系统默认配置文件
        String weatherJson = preferences.getString("weatherJson",null);
        if(weatherJson != null){
            Weather weather = WeatherParseUtil.handWeatherResponse(weatherJson);
            currentCounty = weather.getResult().get(0).getCity();
            showWeatherInfo(weather);
        }else {
            Intent intent = getIntent();
            currentCounty = intent.getStringExtra("county");
            requestWeather(currentCounty);
        }
    }

    private void loadBackground() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //获取系统默认配置文件
        String imageURL = preferences.getString("imageURL",null);
        if(imageURL != null){
            Glide.with(this).load(imageURL).into(background);
        }else{
            requestImage();
        }
    }

    private void requestImage() {
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "服务器请求失败-图片", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String URL = response.body().string();
                if(!TextUtils.isEmpty(URL)){
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("imageURL",URL);
                    editor.apply();
                    runOnUiThread(() -> Glide.with(WeatherActivity.this).load(URL).into(background));
                }
            }
        });
    }


    public void requestWeather( String county) {
        HttpUtil.sendOkHttpRequest("http://apicloud.mob.com/v1/weather/query?key=20588bd8fbea0&city=" + county , new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                        Toast.makeText(WeatherActivity.this, "服务器请求失败—天气", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String weatherJson = response.body().string();
                Weather weather = WeatherParseUtil.handWeatherResponse(weatherJson);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "success".equals(weather.getMsg())){
                            //记录当前城市
                            currentCounty = weather.getResult().get(0).getCity();
                            //记录当前JSon数据和更新时间
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("weatherJson",weatherJson);
                            String updatetime = new SimpleDateFormat("HH:mm").format(System.currentTimeMillis());
                            editor.putString("updateTime",updatetime);
                            editor.apply();
                            //读取更新时间
                            readUpdateTime();
                            //刷新天气界面
                            showWeatherInfo(weather);

                        }else{
                            Toast.makeText(WeatherActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        }
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void readUpdateTime() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //获取系统默认配置文件
        updateTime = preferences.getString("updateTime",null);
    }

    private void showWeatherInfo(Weather weather) {
        //show title
        List<ResultBean> result = weather.getResult();
        String cityName = result.get(0).getCity();
        Date date = new Date();
        titleCity.setText(cityName);

        //show now
        List<FutureBean> future = result.get(0).getFuture();
        String degree = future.get(0).getTemperature();
        String info = future.get(0).getDayTime();
        if(info == null){
            info = future.get(0).getNight();
        }
        nowInfo.setText(info);
        nowDegree.setText(degree);
        if(updateTime != null){
            nowUpdateTime.setText("更新时间 : " + updateTime);
        }


        //show future forecast
        forecastFuture.removeAllViews();
        for(int i =1 ; i < future.size() ; i++) {
            FutureBean temp = future.get(i);
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastFuture, false);
            TextView dateFuture = view.findViewById(R.id.item_date);
            TextView weatherFuture = view.findViewById(R.id.item_weather);
            TextView maxFuture = view.findViewById(R.id.item_max);
            TextView minFuture = view.findViewById(R.id.item_min);
            dateFuture.setText(temp.getWeek());
            weatherFuture.setText(temp.getDayTime());
            maxFuture.setText(temp.getTemperature().split("/")[0]);
            minFuture.setText(temp.getTemperature().split("/")[1]);
            forecastFuture.addView(view);
        }

            //show aqi
            AirQualityBean airQualityBean = result.get(0).getAirQuality();
            aqi.setText(String.valueOf(airQualityBean.getAqi()));
            pm25.setText(String.valueOf(airQualityBean.getPm25()));

            //suggest
            dressingIndex.setText("穿衣指数 ："+result.get(0).getDressingIndex());
            coldIndex.setText("感冒指数 ："+result.get(0).getColdIndex());
            exerciseIndex.setText("运动指数 ："+result.get(0).getExerciseIndex());

    }

    private void findView() {
        titleCity = findViewById(R.id.title_city);
       nowUpdateTime = findViewById(R.id.now_updateTime);
        nowDegree = findViewById(R.id.now_degree);
        nowInfo = findViewById(R.id.now_info);
        forecastFuture = findViewById(R.id.forecast_future);
        aqi = findViewById(R.id.aqi_AQI);
        pm25 = findViewById(R.id.aqi_pm2_5);
        dressingIndex = findViewById(R.id.suggest_dressingIndex);
        coldIndex = findViewById(R.id.suggest_coldIndex);
        exerciseIndex = findViewById(R.id.suggest_exerciseIndex);
        background = findViewById(R.id.weather_background);
        refreshLayout = findViewById(R.id.weather_refresh);
        titleNva = findViewById(R.id.title_nav);
        drawerLayout = findViewById(R.id.weather_drawLayout);
        titlePosition = findViewById(R.id.title_position);
    }

}
