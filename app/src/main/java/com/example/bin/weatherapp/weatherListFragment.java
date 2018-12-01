package com.example.bin.weatherapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.Gson;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.bin.weatherapp.weatherSpider.getForecastWeather;
import static com.example.bin.weatherapp.weatherSpider.getRealTimeWeather;

/**
 * @Author: Bhy
 * @Date: 2018/11/24
 */
public class weatherListFragment extends Fragment{
    private RecyclerView mRecyclerView;
    private myAdapter mAdapter;
    private List<weatherForecast.Daily_forecast> mDatas;
    private weatherRealTime mWeatherRealTime;
    public static String location = "121.6544,25.1552";
    private static final String TAG = "weatherListFragment";
    public static String sLocation = "岳麓区 清水路";
    private TextView temperatrueNow;
    private TextView locationNow;
    private TextView skyConNow;
    private TextView skyIcon;

    public void setupAdapter(){
        if(isAdded()){
            mAdapter = new myAdapter(mDatas);
            mRecyclerView.setAdapter(mAdapter);
            //设置点击事件的处理逻辑，点击后跳转到新的Activity，并传递了对应的DailyForecast对象。
            mAdapter.setMyItemClickListener(new myAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent(getContext(),forecastMsg.class);
                    intent.putExtra("data",new Gson().toJson(mDatas.get(position)));
                    startActivity(intent);
                }
            });
        }
    }

    private class fetchForecastTask extends AsyncTask<Void, Void, List<weatherForecast.Daily_forecast>>{
        //后台线程获取天气预测值
        @Override
        protected List<weatherForecast.Daily_forecast> doInBackground(Void... voids) {
            mDatas = new ArrayList<>();
            List<weatherForecast.Daily_forecast> daily_forecasts = null;
            try{
                daily_forecasts = new weatherSpider().getForecastWeather(location);
            }catch (Exception e){
                e.printStackTrace();
            }
            return daily_forecasts;
        }

        @Override
        protected void onPostExecute(List<weatherForecast.Daily_forecast> daily_forecasts) {
            //将获得的数据绑定到mData中
            mDatas = daily_forecasts;
            mDatas.remove(0);//去掉当天的天气
            Log.i(TAG, "onPostExecute: size is "+mDatas.size());
            //数据获取完毕后通过Adapter显示上去
            setupAdapter();
        }
    }

    //后台异步线程获取实时天气情况
    private class fetchNowTask extends AsyncTask<Void, Void, weatherRealTime>{
        @Override
        protected weatherRealTime doInBackground(Void... voids) {
            try{
                Log.i(TAG, "doInBackground: Location is "+location);
                mWeatherRealTime = getRealTimeWeather(location);
            }catch (Exception e){
                e.printStackTrace();
            }
            return mWeatherRealTime;
        }

        @Override
        protected void onPostExecute(weatherRealTime weatherRealTime) {
            temperatrueNow.setText(weatherRealTime.getTmp()+"°");
            locationNow.setText(sLocation);
            skyConNow.setText(weatherRealTime.getCond_txt());
            skyIcon.setBackgroundResource(myAdapter.getResId(weatherRealTime.getCond_code()));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //后台线程获取天气数据
        new fetchForecastTask().execute();
        new fetchNowTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_list_fragment,container,false);
        //绑定mRecyclerView并设置layoutManager
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //绑定控件
        temperatrueNow = view.findViewById(R.id.temperature_textView);
        locationNow = view.findViewById(R.id.location_textView);
        skyConNow = view.findViewById(R.id.skyCon_textView);
        skyIcon = view.findViewById(R.id.skyCon_frag);

        return view;
    }

}
