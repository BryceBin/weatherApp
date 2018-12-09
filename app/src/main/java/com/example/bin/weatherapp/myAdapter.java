package com.example.bin.weatherapp;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @Author: Bhy
 * @Date: 2018/11/30
 */
public class myAdapter extends RecyclerView.Adapter<myViewHolder>{
    private List<weatherForecast.Daily_forecast> data;
    private static final String TAG = "myAdapter";
    private MyItemClickListener mMyItemClickListener;

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        mMyItemClickListener = myItemClickListener;
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    public myAdapter(List<weatherForecast.Daily_forecast> mdatas){
        this.data = mdatas;
    }
    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //将单个天气预测布局实例化
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_forecast, parent, false);
        //生成viewHolder对象并传递监听器进去
        myViewHolder holder = new myViewHolder(view, mMyItemClickListener);
        return holder;
    }

    public static int getResId(String code){
        //根据天气代码返回对应天气图片的id
        int id = R.drawable.p100;
        try {
            Field field = R.drawable.class.getDeclaredField("p"+code);
            field.setAccessible(true);
            id = field.getInt(field.getName());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        weatherForecast.Daily_forecast daily_forecast = data.get(position);

        //显示对应的信息
        holder.condPic.setBackgroundResource(getResId(daily_forecast.getCond_code_d()));
        holder.skyCon.setText(daily_forecast.getCond_txt_d());

        if (MainActivity.isPhone){
            if (position==0){
                holder.date.setText(R.string.tomorrow);
            }
            else{
                holder.date.setText(forecastMsg.getWeekDay(daily_forecast.getDate()));
            }
        }
        else{
            if (position==0){
                holder.date.setText("今天");
            }
            else if (position==1){
                holder.date.setText(R.string.tomorrow);
            }
            else{
                holder.date.setText(forecastMsg.getWeekDay(daily_forecast.getDate()));
            }
        }



        holder.tmpMax.setText(weatherListFragment.celsiusToFahrenheit(daily_forecast.getTmp_max())+weatherListFragment.tempUnit);
        holder.tmpMin.setText(weatherListFragment.celsiusToFahrenheit(daily_forecast.getTmp_min())+weatherListFragment.tempUnit);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
