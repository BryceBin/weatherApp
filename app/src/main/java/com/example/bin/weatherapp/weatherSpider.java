package com.example.bin.weatherapp;

import android.provider.DocumentsContract;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;


/**
 * @Author: Bhy
 * @Date: 2018/11/25
 */
public class weatherSpider {
    public static weatherRealTime mWeatherRealTime;

    public static weatherRealTime getRealTimeWeather(String location){
        try {
            Connection.Response response = Jsoup.connect("https://api.caiyunapp.com/v2/AtNnPO587kFSQOHr/"+location+"/realtime.json")
                                        .ignoreContentType(true)
                                        .execute();
            //只取result下的数据
            String json = response.body();
            int index = json.indexOf("\"result\"");
            json = json.substring(index+9);
            json = json.substring(0,json.length()-1);
            //System.out.println(json);

            mWeatherRealTime = new Gson().fromJson(json, weatherRealTime.class);
            System.out.println(mWeatherRealTime.getSkycon()+"\n"+
                                mWeatherRealTime.getTemperature()+"\n"+
                                mWeatherRealTime.getCloudrate()+"\n"+
                                mWeatherRealTime.getWind().getSpeed()+"\n"+
                                mWeatherRealTime.getComfort().getIndex()+"\n");

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return mWeatherRealTime;
    }

    public static void getForecastWeather(String location){
        try{
            Connection.Response response = Jsoup.connect("https://api.caiyunapp.com/v2/AtNnPO587kFSQOHr/"+location+"/forecast.json")
                                                    .ignoreContentType(true)
                                                    .execute();
            String json = response.body();
            int index1 = json.indexOf("\"daily\"");
            int index2 = json.indexOf("\"primary\"");
            json = json.substring(index1+8,index2-1);
            System.out.println(json);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
