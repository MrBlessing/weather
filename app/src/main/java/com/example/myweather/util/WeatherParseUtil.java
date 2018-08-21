package com.example.myweather.util;

import com.example.myweather.gson.Weather;
import com.google.gson.Gson;

public class WeatherParseUtil {
    public static Weather handWeatherResponse(String response){
        Gson gson = new Gson();
        Weather weather = gson.fromJson( response ,Weather.class );
        return weather;
    }
}
