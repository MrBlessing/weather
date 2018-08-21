package com.example.myweather.gson;

public class FetureDataBean {
    /**
     * aqi : 62
     * date : 2018-08-22
     * quality : 良
     */

    private int aqi;
    private String date;
    private String quality;

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}
