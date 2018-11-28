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
    @Test
    public void getRealTimeWeatherTest() {
        mWeatherSpider.getRealTimeWeather(mLocation);
    }

    @Test
    public void getForecastWeatherTest(){
        mWeatherSpider.getForecastWeather(mLocation);
    }
}