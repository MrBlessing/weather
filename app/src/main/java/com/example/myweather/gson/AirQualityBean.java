package com.example.myweather.gson;

import java.util.List;

public class AirQualityBean {
    /**
     * aqi : 40
     * city : 黄冈
     * district : 黄冈
     * fetureData : [{"aqi":62,"date":"2018-08-22","quality":"良"},{"aqi":50,"date":"2018-08-23","quality":"优"},{"aqi":49,"date":"2018-08-24","quality":"优"},{"aqi":50,"date":"2018-08-25","quality":"优"},{"aqi":47,"date":"2018-08-26","quality":"优"}]
     * hourData : [{"aqi":40,"dateTime":"2018-08-21 14:00:00"},{"aqi":41,"dateTime":"2018-08-21 13:00:00"},{"aqi":40,"dateTime":"2018-08-21 12:00:00"},{"aqi":36,"dateTime":"2018-08-21 11:00:00"},{"aqi":33,"dateTime":"2018-08-21 10:00:00"},{"aqi":34,"dateTime":"2018-08-21 09:00:00"},{"aqi":38,"dateTime":"2018-08-21 08:00:00"},{"aqi":40,"dateTime":"2018-08-21 07:00:00"},{"aqi":40,"dateTime":"2018-08-21 06:00:00"},{"aqi":36,"dateTime":"2018-08-21 05:00:00"},{"aqi":39,"dateTime":"2018-08-21 04:00:00"},{"aqi":36,"dateTime":"2018-08-21 03:00:00"},{"aqi":34,"dateTime":"2018-08-21 02:00:00"},{"aqi":37,"dateTime":"2018-08-21 01:00:00"},{"aqi":36,"dateTime":"2018-08-21 00:00:00"},{"aqi":34,"dateTime":"2018-08-20 23:00:00"},{"aqi":37,"dateTime":"2018-08-20 22:00:00"},{"aqi":38,"dateTime":"2018-08-20 21:00:00"},{"aqi":30,"dateTime":"2018-08-20 20:00:00"},{"aqi":26,"dateTime":"2018-08-20 19:00:00"},{"aqi":30,"dateTime":"2018-08-20 18:00:00"},{"aqi":30,"dateTime":"2018-08-20 17:00:00"},{"aqi":30,"dateTime":"2018-08-20 16:00:00"},{"aqi":30,"dateTime":"2018-08-20 15:00:00"}]
     * no2 : 3
     * pm10 : 40
     * pm25 : 24
     * province : 湖北
     * quality : 优
     * so2 : 4
     * updateTime : 2018-08-21 15:00:00
     */

    private int aqi;
    private String city;
    private String district;
    private int no2;
    private int pm10;
    private int pm25;
    private String province;
    private String quality;
    private int so2;
    private String updateTime;
    private List<FetureDataBean> fetureData;
    private List<HourDataBean> hourData;

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getNo2() {
        return no2;
    }

    public void setNo2(int no2) {
        this.no2 = no2;
    }

    public int getPm10() {
        return pm10;
    }

    public void setPm10(int pm10) {
        this.pm10 = pm10;
    }

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public int getSo2() {
        return so2;
    }

    public void setSo2(int so2) {
        this.so2 = so2;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<FetureDataBean> getFetureData() {
        return fetureData;
    }

    public void setFetureData(List<FetureDataBean> fetureData) {
        this.fetureData = fetureData;
    }

    public List<HourDataBean> getHourData() {
        return hourData;
    }

    public void setHourData(List<HourDataBean> hourData) {
        this.hourData = hourData;
    }
}