package com.example.myweather;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.myweather.service.autoUpdateService;
import com.example.myweather.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //启动更新天气服务
        Intent service = new Intent(this, autoUpdateService.class);
        startService(service);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherJson = preferences.getString("weatherJson",null);
        if(weatherJson != null){
            //有缓存就直接进入天气界面
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }else {
            //没有缓存就尝试自动定位
            if(applyForPermission()){
                autoPosition();
            }
            //无法自动定位就留在这个页面手动选中位置
        }
    }


    private void autoPosition() {
        LocationClient client = new LocationClient(this);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        client.setLocOption(option);
        client.registerLocationListener(new BDAbstractLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                String county = null;
                if (bdLocation.getDistrict().contains("县")) {
                    county = bdLocation.getDistrict().split("县")[0];
                } else if (bdLocation.getDistrict().contains("区")) {
                    county = bdLocation.getDistrict().split("区")[0];
                } else {
                    Toast.makeText(MainActivity.this, "本软件暂时无法定位当前地区", Toast.LENGTH_SHORT).show();
                }
                LogUtil.d(TAG, "county :" + county);
                if (bdLocation.getDistrict().contains("市辖")) {
                    county = null;
                    Toast.makeText(MainActivity.this, "本软件暂时无法定位当前地区", Toast.LENGTH_SHORT).show();
                }
                if (county != null) {
                    Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                    intent.putExtra("county", county);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
                client.stop();
            }
        });
        client.start();
    }

    private boolean applyForPermission() {
        boolean result = false ;
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!permissionList.isEmpty()){
            String[] permission =  permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,permission,1);
        }else {
            result = true;
        }
        return result;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能自动定位功能", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    autoPosition();
                }break;

            default: break;
        }
    }

}
