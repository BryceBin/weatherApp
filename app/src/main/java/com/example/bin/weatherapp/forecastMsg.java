package com.example.bin.weatherapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class forecastMsg extends Fragment {
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


    public void init(View view){
        //绑定控件
        icon = view.findViewById(R.id.skyCon_msg);
        date = view.findViewById(R.id.date_msg);
        weekday = view.findViewById(R.id.weekday_msg);
        temperature = view.findViewById(R.id.temperature_msg);
        wind = view.findViewById(R.id.wind_msg);
        hum = view.findViewById(R.id.hum_msg);
        pres = view.findViewById(R.id.pres_msg);
        vis = view.findViewById(R.id.vis_msg);
        skyCon = view.findViewById(R.id.weather_msg);
    }

    public void setTitleBar(){
        ActionBar actionBar = MainActivity.sActionBar;
        actionBar.setTitle(R.string.weatherDetail);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.getCustomView().findViewById(R.id.share_detail).setVisibility(View.VISIBLE);
//        actionBar.getCustomView().findViewById(android.R.id.home).setVisibility(View.VISIBLE);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_forecast_msg,container,false);

        if (MainActivity.isPhone){
            Fragment fragment = getFragmentManager().findFragmentByTag(MainActivity.getTag());
            getFragmentManager().beginTransaction().hide(fragment).commit();
            setTitleBar();
        }

        init(view);
        mDaily_forecast = new Gson().fromJson(getArguments().getString("data"), weatherForecast.Daily_forecast.class);
        setData();



        return view;
    }

    public static forecastMsg newInstance(String json) {
        Bundle args = new Bundle();
        forecastMsg fragment = new forecastMsg();
        args.putString("data",json);
        fragment.setArguments(args);
        return fragment;
    }

    private void setData(){
        icon.setBackgroundResource(myAdapter.getResId(mDaily_forecast.getCond_code_d()));
        String[] dates = mDaily_forecast.getDate().split("-");
        date.setText(dates[1]+"月"+dates[2]+"日");
        weekday.setText(getWeekDay(mDaily_forecast.getDate()));
        temperature.setText(weatherListFragment.celsiusToFahrenheit(mDaily_forecast.getTmp_min())+weatherListFragment.tempUnit+"~"+weatherListFragment.celsiusToFahrenheit(mDaily_forecast.getTmp_max())+weatherListFragment.tempUnit);
        wind.setText(mDaily_forecast.getWind_dir()+mDaily_forecast.getWind_sc()+"级");
        hum.setText("相对湿度:"+mDaily_forecast.getHum());
        pres.setText("大气压强:"+mDaily_forecast.getPres()+"Kpa");
        vis.setText("能见度:"+mDaily_forecast.getVis()+"km");
        skyCon.setText(mDaily_forecast.getCond_txt_d());
    }



    @Override
    public void onDestroyView() {
        if (MainActivity.isPhone){
            Fragment fragment = getFragmentManager().findFragmentByTag(MainActivity.getTag());
            MainActivity.tags.remove(MainActivity.tags.size()-1);
            weatherListFragment.setTitleBar();
            getFragmentManager().beginTransaction().show(fragment).commit();
        }

        super.onDestroyView();
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
