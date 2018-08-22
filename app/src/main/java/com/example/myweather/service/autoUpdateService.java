package com.example.myweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.myweather.gson.Weather;
import com.example.myweather.util.HttpUtil;
import com.example.myweather.util.LogUtil;
import com.example.myweather.util.WeatherParseUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class autoUpdateService extends Service {
    private static final String TAG = "autoUpdateService";
    public autoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG,"start Service");
        updateWeather();
        updateImage();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int hour = 1000 * 60 * 60;//一小时
        long triggerAtTime = SystemClock.elapsedRealtime() + hour ;
        Intent intent1 = new Intent(this,autoUpdateService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,0,intent1,0);
        manager.cancel(pendingIntent);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //获取系统默认配置文件
        String weatherJson = preferences.getString("weatherJson",null);
        if(weatherJson != null) {
            Weather weather = WeatherParseUtil.handWeatherResponse(weatherJson);
            String currentCity = weather.getResult().get(0).getCity();
            HttpUtil.sendOkHttpRequest("http://apicloud.mob.com/v1/weather/query?key=20588bd8fbea0&city=" + currentCity, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String weatherJson = response.body().string();
                    Weather weather = WeatherParseUtil.handWeatherResponse(weatherJson);
                    if (weather != null && "success".equals(weather.getMsg())) {
                        //记录当前JSon数据和更新时间
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(autoUpdateService.this);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("weatherJson", weatherJson);
                        String updatetime = new SimpleDateFormat("HH:mm").format(System.currentTimeMillis());
                        editor.putString("updateTime", updatetime);
                        editor.apply();
                    }
                }
            });
        }
    }
    private void updateImage(){
        HttpUtil.sendOkHttpRequest("http://guolin.tech/api/bing_pic", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String URL = response.body().string();
                if(!TextUtils.isEmpty(URL)){
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(autoUpdateService.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("imageURL",URL);
                    editor.apply();
                }
            }
        });
    }
}