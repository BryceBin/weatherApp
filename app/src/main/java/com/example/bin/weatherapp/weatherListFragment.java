package com.example.bin.weatherapp;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.Gson;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public static weatherRealTime mWeatherRealTime;
    public static String location = "112.98227692,28.19408989";
    private static final String TAG = "weatherListFragment";
    public static String sLocation = "岳麓区 清水路";
    private TextView temperatrueNow;
    private TextView locationNow;
    private TextView skyConNow;
    private TextView skyIcon;
    private ConstraintLayout mConstraintLayout;
    private weatherForecast.Daily_forecast mToday;
    private Handler mHandler;
    private String tempToday;
    public static boolean needUpdate = false;
    public static boolean tempUnitChanged = false;
    public static boolean noteChanged = false;

    private static final String NOTIFICATION_CHANNEL = "myChannel";

    public static NotificationChannel genChannel(String channel){
        NotificationChannel channel1 = null;
        if (Build.VERSION.SDK_INT>=26){
            channel1 = new NotificationChannel(channel,"channel1",NotificationManager.IMPORTANCE_DEFAULT);
            channel1.enableLights(true);
            channel1.setLightColor(Color.RED);
            channel1.setShowBadge(true);
        }

        return channel1;
    }

    private class fetchNewWeatherThread implements Runnable{
        @Override
        public void run() {
            while(true){
                if (needUpdate){
                    Log.i(TAG, "run: get new weather");
                    new fetchNowTask().execute();
                    new fetchForecastTask().execute();
                    needUpdate = false;
                }
                if (tempUnitChanged){
                    Log.i(TAG, "run: change temp unit");
                    //mAdapter.notifyDataSetChanged();
                    mHandler.sendEmptyMessage(1);
                    tempUnitChanged = false;
                }
                if (noteChanged){
                    Log.i(TAG, "run: in the out thread");
                    // 设置通知栏
                    if (MainActivity.isNoteOn){
//                        Intent intent = new Intent(getActivity(),AlarmReceiver.class);
//                        intent.setAction("notification");
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),0,intent,0);
//                        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
//                        int type = AlarmManager.RTC_WAKEUP;
//                        long triggerAtMillis = new Date().getTime();
//                        long intervalMillis = 1000 * 60;
//                        alarmManager.setInexactRepeating(type, triggerAtMillis, intervalMillis, pendingIntent);
//                        Log.i(TAG, "run: in thread ");
                        Context context = getActivity();
                        //创建一个通知管理器
//                        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                        if (Build.VERSION.SDK_INT>=26){
//                            //API26以上必须自定义NotificationChannel
//                            notificationManager.createNotificationChannel(genChannel(NOTIFICATION_CHANNEL));
//
//                        }
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL);
//                        builder .setTicker("天气预报")
//                                .setSmallIcon(R.drawable.title_bar_icon)
//                                .setWhen(System.currentTimeMillis())
//                                .setStyle(new NotificationCompat.InboxStyle()
//                                                .setBigContentTitle("小天气")
//                                                .setSummaryText("summaryText")
//                                                .addLine(String.format("气温:%s%s~%s%s",celsiusToFahrenheit(mToday.getTmp_min()),tempUnit,celsiusToFahrenheit(mToday.getTmp_max()),tempUnit))
//                                                .addLine(String.format("降水量:%s",mToday.getPcpn()))
//                                                .addLine(String.format("紫外线强度:%s",mToday.getUv_index()))
//                                                .addLine(String.format("%s%s",mToday.getWind_dir(),mToday.getWind_sc())))
//                                .setDefaults(Notification.DEFAULT_ALL);
//                        Notification notification = builder.build();
//                        notificationManager.notify(10,notification);
                    }

                    noteChanged = false;
                }
            }
        }
    }


    public static String tempUnit = "°C";

    public static String formatShareText(){
        //点击分享后的内容
        return String.format("位置:%s\n" +
                            "经纬度:%s\n" +
                            "温度:%s"+tempUnit+"\n" +
                            "天气状况:%s\n" +
                            "体感温度:%s\n" +
                            "相对湿度:%s\n" +
                            "能见度:%s km\n" +
                            "大气压强:%s kpa",sLocation,location,mWeatherRealTime.getTmp(),mWeatherRealTime.getCond_txt(),
                                            mWeatherRealTime.getFl(),mWeatherRealTime.getHum(),mWeatherRealTime.getVis(),mWeatherRealTime.getPres()
        );
    }



    public void setupAdapter(){
        if(isAdded()){
            mAdapter = new myAdapter(mDatas);
            mRecyclerView.setAdapter(mAdapter);

            if (!MainActivity.isPhone){
                //平板的话直接显示今天的预测天气
                forecastMsg forecastMsg = com.example.bin.weatherapp.forecastMsg.newInstance(new Gson().toJson(mDatas.get(0)));
                getFragmentManager().beginTransaction()
                        //.addToBackStack(null)
                        .add(R.id.forecast_fragment_container,forecastMsg,"forecastTag").commit();
            }

            //设置点击事件的处理逻辑，点击后跳转到新的Fragment，并传递了对应的DailyForecast对象。
            mAdapter.setMyItemClickListener(new myAdapter.MyItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    forecastMsg fragment = forecastMsg.newInstance(new Gson().toJson(mDatas.get(position)));
                    if (MainActivity.isPhone){
                        Log.i(TAG, "onItemClick: 是手机");
                        MainActivity.tags.add("forecastTag");
                        getFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.fragment_container,fragment,"forecastTag").commit();
                    }
                    else{
                        Log.i(TAG, "onItemClick: 是平板");
                        getFragmentManager().beginTransaction()
                                //.addToBackStack(null)
                                .replace(R.id.forecast_fragment_container,fragment,"forecastTag").commit();
                    }

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
            if(MainActivity.isPhone){
                mToday = mDatas.get(0);//获取当天的预测值
                mDatas.remove(0);//去掉当天的天气
            }

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
            temperatrueNow.setText(celsiusToFahrenheit(weatherRealTime.getTmp())+tempUnit);
            tempToday = weatherRealTime.getTmp();
            locationNow.setText(sLocation);
            skyConNow.setText(weatherRealTime.getCond_txt());
            skyIcon.setBackgroundResource(myAdapter.getResId(weatherRealTime.getCond_code()));
            //设置点击后跳转到当天天气具体情况页面
            mConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mToday!=null){
                        forecastMsg forecastMsg = com.example.bin.weatherapp.forecastMsg.newInstance(new Gson().toJson(mToday));
                        //forecastMsg.setTargetFragment(this,3);
                        if (MainActivity.isPhone){
                            MainActivity.tags.add("forecastTag");
                            getFragmentManager().beginTransaction()
                                    .addToBackStack(null)
                                    .add(R.id.fragment_container,forecastMsg,"forecastTag").commit();
                        }
                        else{
                            getFragmentManager().beginTransaction()
                                    .addToBackStack(null)
                                    .replace(R.id.forecast_fragment_container,forecastMsg,"forecastTag").commit();
                        }

                    }
                }
            });
        }
    }

    public static void setTitleBar(){
        ActionBar actionBar = MainActivity.sActionBar;
        actionBar.setTitle(R.string.app_name);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

//        actionBar.getCustomView().findViewById(android.R.id.home).setVisibility(View.INVISIBLE);
//        actionBar.getCustomView().findViewById(R.id.share_detail).setVisibility(View.INVISIBLE);

    }


    public static String celsiusToFahrenheit(String temp){
        if(!MainActivity.isCelsius){
            int old = Integer.parseInt(temp);
            double newTemp = 32 + old*1.8;
            String res = String.valueOf(newTemp);
            if (res.length()>5){
                res = res.substring(0,4);
            }
            return res;
        }
        return temp;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        //开启线程监听位置是否改变
        new Thread(new fetchNewWeatherThread()).start();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what==1){
                    mAdapter.notifyDataSetChanged();
                    temperatrueNow.setText(celsiusToFahrenheit(tempToday)+tempUnit);
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weather_list_fragment,container,false);
        //后台线程获取天气数据
        new fetchForecastTask().execute();
        if(MainActivity.isPhone){
            new fetchNowTask().execute();
        }

        //绑定mRecyclerView并设置layoutManager
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));



        if (MainActivity.isPhone){
            //绑定控件
            temperatrueNow = view.findViewById(R.id.temperature_textView);
            locationNow = view.findViewById(R.id.location_textView);
            skyConNow = view.findViewById(R.id.skyCon_textView);
            skyIcon = view.findViewById(R.id.skyCon_frag);
            mConstraintLayout = view.findViewById(R.id.constraintLayout);
        }

        setTitleBar();

        return view;
    }

}
