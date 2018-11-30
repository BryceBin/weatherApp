package com.example.bin.weatherapp;

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
    private HomeAdapter mAdapter;
    private List<weatherForecast.Daily_forecast> mDatas;
    private weatherRealTime mWeatherRealTime;
    private static String localtion = "121.6544,25.1552";
    private static final String TAG = "weatherListFragment";
    private TextView temperatrueNow;
    private TextView localtionNow;
    private TextView skyConNow;

    public void setupAdapter(){
        if(isAdded()){
            mAdapter = new HomeAdapter(mDatas);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private class fetchForecastTask extends AsyncTask<Void, Void, List<weatherForecast.Daily_forecast>>{
        //后台线程获取天气预测值
        @Override
        protected List<weatherForecast.Daily_forecast> doInBackground(Void... voids) {
            mDatas = new ArrayList<>();
            List<weatherForecast.Daily_forecast> daily_forecasts = null;
            try{
                daily_forecasts = new weatherSpider().getForecastWeather(localtion);
            }catch (Exception e){
                e.printStackTrace();
            }
            return daily_forecasts;
        }

        @Override
        protected void onPostExecute(List<weatherForecast.Daily_forecast> daily_forecasts) {
            //将获得的数据绑定到mData中
            mDatas = daily_forecasts;
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
                mWeatherRealTime = getRealTimeWeather(localtion);
            }catch (Exception e){
                e.printStackTrace();
            }
            return mWeatherRealTime;
        }

        @Override
        protected void onPostExecute(weatherRealTime weatherRealTime) {
            temperatrueNow.setText(weatherRealTime.getTmp()+"°C");
            //localtionNow.setText(weatherRealTime.);
            skyConNow.setText(weatherRealTime.getCond_txt());
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
        localtionNow = view.findViewById(R.id.location_textView);
        skyConNow = view.findViewById(R.id.skyCon_textView);

        return view;
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
        private List<weatherForecast.Daily_forecast> data;
        public HomeAdapter(List<weatherForecast.Daily_forecast> mdatas){
            this.data = mdatas;
        }
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //将单个天气预测布局实例化
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.single_forecast, parent,
                    false));
            return holder;
        }

        int getResId(String code){
            //根据天气代码返回对应天气图片的id
            code = "p"+code;
            return getResources().getIdentifier(code,"drawable",getActivity().getApplication().getPackageName());
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            weatherForecast.Daily_forecast daily_forecast = data.get(position);
            //显示对应的信息
            holder.condPic.setBackgroundResource(getResId(daily_forecast.getCond_code_d()));
            holder.skyCon.setText(daily_forecast.getCond_txt_d());
            holder.date.setText(daily_forecast.getDate());
            holder.tmpMax.setText(daily_forecast.getTmp_max()+"°C");
            holder.tmpMin.setText(daily_forecast.getTmp_min()+"°C");
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView condPic;
            TextView skyCon;
            TextView date;
            TextView tmpMax;
            TextView tmpMin;
            public MyViewHolder(View view) {
                super(view);
                try{
                    //绑定空间
                    condPic = view.findViewById(R.id.skyConPic_textView);
                    skyCon = view.findViewById(R.id.skyCon);
                    date = view.findViewById(R.id.date_textView);
                    tmpMax = view.findViewById(R.id.tmp_max);
                    tmpMin = view.findViewById(R.id.tmp_min);
                }catch (Exception e){
                    Log.i(TAG, "MyViewHolder: find view fail here");
                    e.printStackTrace();
                }

            }
        }
    }
}
