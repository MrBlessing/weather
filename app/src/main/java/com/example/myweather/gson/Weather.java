package com.example.myweather.gson;

import java.util.List;

public class Weather {


    /**
     * msg : success
     * result : [{"airCondition":"优","airQuality":{"aqi":40,"city":"黄冈","district":"黄冈","fetureData":[{"aqi":62,"date":"2018-08-22","quality":"良"},{"aqi":50,"date":"2018-08-23","quality":"优"},{"aqi":49,"date":"2018-08-24","quality":"优"},{"aqi":50,"date":"2018-08-25","quality":"优"},{"aqi":47,"date":"2018-08-26","quality":"优"}],"hourData":[{"aqi":40,"dateTime":"2018-08-21 14:00:00"},{"aqi":41,"dateTime":"2018-08-21 13:00:00"},{"aqi":40,"dateTime":"2018-08-21 12:00:00"},{"aqi":36,"dateTime":"2018-08-21 11:00:00"},{"aqi":33,"dateTime":"2018-08-21 10:00:00"},{"aqi":34,"dateTime":"2018-08-21 09:00:00"},{"aqi":38,"dateTime":"2018-08-21 08:00:00"},{"aqi":40,"dateTime":"2018-08-21 07:00:00"},{"aqi":40,"dateTime":"2018-08-21 06:00:00"},{"aqi":36,"dateTime":"2018-08-21 05:00:00"},{"aqi":39,"dateTime":"2018-08-21 04:00:00"},{"aqi":36,"dateTime":"2018-08-21 03:00:00"},{"aqi":34,"dateTime":"2018-08-21 02:00:00"},{"aqi":37,"dateTime":"2018-08-21 01:00:00"},{"aqi":36,"dateTime":"2018-08-21 00:00:00"},{"aqi":34,"dateTime":"2018-08-20 23:00:00"},{"aqi":37,"dateTime":"2018-08-20 22:00:00"},{"aqi":38,"dateTime":"2018-08-20 21:00:00"},{"aqi":30,"dateTime":"2018-08-20 20:00:00"},{"aqi":26,"dateTime":"2018-08-20 19:00:00"},{"aqi":30,"dateTime":"2018-08-20 18:00:00"},{"aqi":30,"dateTime":"2018-08-20 17:00:00"},{"aqi":30,"dateTime":"2018-08-20 16:00:00"},{"aqi":30,"dateTime":"2018-08-20 15:00:00"}],"no2":3,"pm10":40,"pm25":24,"province":"湖北","quality":"优","so2":4,"updateTime":"2018-08-21 15:00:00"},"city":"黄冈","coldIndex":"易发期","date":"2018-08-21","distrct":"黄冈","dressingIndex":"薄短袖类","exerciseIndex":"不适宜","future":[{"date":"2018-08-21","dayTime":"多云","night":"多云","temperature":"33°C / 24°C","week":"今天","wind":"东北风 小于3级"},{"date":"2018-08-22","dayTime":"多云","night":"阵雨","temperature":"33°C / 25°C","week":"星期三","wind":"北风 小于3级"},{"date":"2018-08-23","dayTime":"多云","night":"晴","temperature":"32°C / 22°C","week":"星期四","wind":"北风 3～4级"},{"date":"2018-08-24","dayTime":"阴","night":"阴","temperature":"33°C / 24°C","week":"星期五","wind":"东南风 3～4级"},{"date":"2018-08-25","dayTime":"阴","night":"阴","temperature":"33°C / 21°C","week":"星期六","wind":"东南风 3～4级"},{"date":"2018-08-26","dayTime":"阴","night":"阴","temperature":"34°C / 25°C","week":"星期日","wind":"东南风 3～4级"}],"humidity":"湿度：51%","pollutionIndex":"40","province":"湖北","sunrise":"05:31","sunset":"19:23","temperature":"33℃","time":"15:12","updateTime":"20180821152955","washIndex":"不太适宜","weather":"晴","week":"周二","wind":"北风2级"}]
     * retCode : 200
     */

    private String msg;
    private String retCode;
    private List<ResultBean> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

}
