package com.example.myweather.util;

import android.text.TextUtils;

import com.example.myweather.db.City;
import com.example.myweather.db.County;
import com.example.myweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RegionParseUtil {
    /*
    * 使用JSONObject处理省级数据
    * */
    public static boolean handleProvinceResponse(String response) {
        if(!TextUtils.isEmpty(response)){
            //这个方法可以判断字符串是否为null或者空
            try {
                JSONArray allProvince = new JSONArray(response);
                for(int i=0;i<allProvince.length();i++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /*
     * 使用JSONObject处理市级数据
     * */
    public static boolean handleCityResponse(String response , int provinceId) {
        if(!TextUtils.isEmpty(response)){
            //这个方法可以判断字符串是否为null或者空
            try {
                JSONArray allCity = new JSONArray(response);
                for(int i=0;i<allCity.length();i++){
                    JSONObject cityObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /*
     * 使用JSONObject处理县级数据
     * */
    public static boolean handleCountyResponse(String response , int cityId) {
        if(!TextUtils.isEmpty(response)){
            //这个方法可以判断字符串是否为null或者空
            try {
                JSONArray allCounty = new JSONArray(response);
                for(int i=0;i<allCounty.length();i++){
                    JSONObject countyObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getInt("id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
