package com.example.bin.weatherapp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author: Bhy
 * @Date: 2018/11/26
 */
public class weatherSpiderTest {
    private String mLocation = "121.6544,25.1552";
    weatherSpider mWeatherSpider = new weatherSpider();
    String myJson = "{\"status\":\"ok\",\"lang\":\"zh_CN\",\"unit\":\"metric\",\"server_time\":1543230611,\"location\":[25.1552,121.6544],\"api_status\":\"active\",\"tzshift\":28800,\"api_version\":\"v2.2\",\"result\":{\"status\":\"ok\",\"o3\":100.58,\"co\":0.2782,\"temperature\":21.0,\"pm10\":31.0,\"skycon\":\"RAIN\",\"cloudrate\":0.98,\"aqi\":31,\"dswrf\":22.0,\"visibility\":19.9,\"humidity\":0.88,\"so2\":0.0,\"ultraviolet\":{\"index\":0.0,\"desc\":\"\\u65e0\"},\"pres\":100470.46,\"pm25\":5,\"no2\":6.42,\"precipitation\":{\"nearest\":{\"status\":\"ok\",\"distance\":0.97,\"intensity\":0.5},\"local\":{\"status\":\"ok\",\"intensity\":0.3328,\"datasource\":\"radar\"}},\"comfort\":{\"index\":4,\"desc\":\"\\u6e29\\u6696\"},\"wind\":{\"direction\":25.59,\"speed\":13.19}}}";

    @Test
    public void getRealTimeWeatherTest() {
        mWeatherSpider.getRealTimeWeather(mLocation);
    }

    @Test
    public void getForecastWeatherTest(){
        mWeatherSpider.getForecastWeather(mLocation);
    }
}