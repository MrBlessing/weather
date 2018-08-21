package com.example.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

    private ScrollView scrollView;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView nowDegree;
    private TextView nowInfo;
    private LinearLayout forecastFuture;
    private static final String TAG = "WeatherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        findView();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //获取系统默认配置文件
        String weatherJson = preferences.getString("weatherJson",null);
        if(weatherJson != null){
            Weather weather = WeatherParseUtil.handWeatherResponse(weatherJson);
            showWeatherInfo(weather);
        }else {
            autoPosition();
        }
    }

    private void autoPosition() {
        artificialPosition();
    }

    private void artificialPosition() {
        Intent intent = getIntent();
        String province = intent.getStringExtra("province");
        String city = intent.getStringExtra("city");
        String county = intent.getStringExtra("county");
        HttpUtil.sendOkHttpRequest("http://apicloud.mob.com/v1/weather/query?key=20588bd8fbea0&city=" + county + "&province=" + province, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "查询天气请求失败，请检查网络", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String weatherJson = response.body().string();
                Weather weather = WeatherParseUtil.handWeatherResponse(weatherJson);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "success".equals(weather.getMsg())){
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("weatherJson",weatherJson);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(WeatherActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        //show title
        List<ResultBean> result = weather.getResult();
        String cityName = result.get(0).getCity();
        Date date = new Date();
        String updateTime = new SimpleDateFormat("HH:mm").format(System.currentTimeMillis());
        titleUpdateTime.setText(updateTime);
        titleCity.setText(cityName);

        //show now
        List<FutureBean> future = result.get(0).getFuture();
        String degree = future.get(0).getTemperature();
        String info = future.get(0).getDayTime();
        if("null".equals(info)){
            info = future.get(0).getNight();
        }
        nowInfo.setText(info);
        nowDegree.setText(degree);

        //show future
        forecastFuture.removeAllViews();
        String now = new SimpleDateFormat("y-MM-dd").format(System.currentTimeMillis());
        for(FutureBean temp : future){
            if(now.equals(temp.getDate())){
                continue;
            }
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastFuture,false);
            TextView dateFuture = view.findViewById(R.id.item_date);
            TextView  weatherFuture = view.findViewById(R.id.item_weather);
            TextView maxFuture = view.findViewById(R.id.item_max);
            TextView minFuture = view.findViewById(R.id.item_min);
            dateFuture.setText(temp.getWeek());
            weatherFuture.setText(temp.getDayTime());
            maxFuture.setText(temp.getTemperature().split("/")[0]);
            minFuture.setText(temp.getTemperature().split("/")[1]);
            forecastFuture.addView(view);
        }
    }

    private void findView() {
        scrollView = findViewById(R.id.weather_scrollView);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_updateTime);
        nowDegree = findViewById(R.id.now_degree);
        nowInfo = findViewById(R.id.now_info);
        forecastFuture = findViewById(R.id.forecast_future);
    }

}
