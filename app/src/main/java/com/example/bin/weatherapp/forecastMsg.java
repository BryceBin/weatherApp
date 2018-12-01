package com.example.bin.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class forecastMsg extends AppCompatActivity {
    private TextView icon;
    private TextView date;
    private TextView weekday;
    private TextView temperature;
    private TextView wind;
    private TextView hum;
    private TextView pres;
    private TextView vis;
    private TextView skyCon;
    private weatherForecast.Daily_forecast mDaily_forecast;
    public static String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public void init(){
        //绑定控件
        icon = findViewById(R.id.skyCon_msg);
        date = findViewById(R.id.date_msg);
        weekday = findViewById(R.id.weekday_msg);
        temperature = findViewById(R.id.temperature_msg);
        wind = findViewById(R.id.wind_msg);
        hum = findViewById(R.id.hum_msg);
        pres = findViewById(R.id.pres_msg);
        vis = findViewById(R.id.vis_msg);
        skyCon = findViewById(R.id.weather_msg);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_msg);
        init();
        mDaily_forecast = new Gson().fromJson(getIntent().getStringExtra("data"), weatherForecast.Daily_forecast.class);
        setData();
    }

    private void setData(){
        icon.setBackgroundResource(myAdapter.getResId(mDaily_forecast.getCond_code_d()));
        String[] dates = mDaily_forecast.getDate().split("-");
        date.setText(dates[1]+"月"+dates[2]+"日");
        weekday.setText(getWeekDay(mDaily_forecast.getDate()));
        temperature.setText(mDaily_forecast.getTmp_min()+"°C~"+mDaily_forecast.getTmp_max()+"°C");
        wind.setText(mDaily_forecast.getWind_dir()+mDaily_forecast.getWind_sc()+"级");
        hum.setText("相对湿度:"+mDaily_forecast.getHum());
        pres.setText("大气压强:"+mDaily_forecast.getPres()+"Kpa");
        vis.setText("能见度:"+mDaily_forecast.getVis()+"km");
        skyCon.setText(mDaily_forecast.getCond_txt_d());
    }

    //根据日期返回星期信息
    public static String getWeekDay(String date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        try{
            d = simpleDateFormat.parse(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        int w = calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(w<0)w=0;
        return weekDays[w];
    }
}
