package com.example.bin.weatherapp;

import android.os.AsyncTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.util.ArrayList;

import static com.example.bin.weatherapp.weatherSpider.getRealTimeWeather;
import static org.junit.Assert.*;

/**
 * @Author: Bhy
 * @Date: 2018/11/26
 */
public class weatherSpiderTest {
    private String mLocation = "121.6544,25.1552";
    private String tCity = "北京";
    weatherSpider mWeatherSpider = new weatherSpider();
    private class fetchForecastTask extends AsyncTask<Void, Void, Void> {
        //后台线程获取天气预测值
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                mWeatherSpider.getForecastWeather(mLocation);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }
    @Test
    public void getRealTimeWeatherTest() {
        //mWeatherSpider.getRealTimeWeather(mLocation);
        //new fetchForecastTask().execute();
    }

    @Test
    public void getForecastWeatherTest() throws Exception{
        mWeatherSpider.getForecastWeather(mLocation);
    }
    @Test
    public void getCity() {
        try{
            mWeatherSpider.getCity(tCity);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}