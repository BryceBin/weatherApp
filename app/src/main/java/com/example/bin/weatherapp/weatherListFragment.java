package com.example.bin.weatherapp;

import android.app.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

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
    public static weatherForecast.Daily_forecast mToday = null;
    private Handler mHandler;
    private String tempToday;
    public static boolean needUpdate = false;
    public static boolean tempUnitChanged = false;
    public static boolean noteChanged = false;

    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;


    private static final String NOTIFICATION_CHANNEL = "myChannel";

    //判断是否有网络
    public static boolean isNetworkOn(Context context){
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

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
                    if (isNetworkOn(getContext())){
                        if (MainActivity.isPhone){
                            new fetchNowTask().execute();
                        }
                        new fetchForecastTask().execute();
                    }

                    needUpdate = false;
                }
                if (tempUnitChanged){
                    Log.i(TAG, "run: change temp unit");
                    //mAdapter.notifyDataSetChanged();
                    mHandler.sendEmptyMessage(1);
                    tempUnitChanged = false;
                }
            }
        }
    }


    public static String tempUnit = "°C";

    public static String formatShareText(){
        //点击分享后的内容
        return String.format("位置:%s\n" +
                            "经纬度:%s\n" +
                            "温度:%s"+tempUnit+"~%s"+tempUnit+"\n" +
                            "天气状况:%s\n" +
                            "相对湿度:%s\n" +
                            "能见度:%s km\n" +
                            "大气压强:%s kpa",sLocation,location,mToday.getTmp_min(),mToday.getTmp_max(),
                                            mToday.getCond_txt_d(),mToday.getHum(),mToday.getVis(),mToday.getPres()
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


    //先将之前的数据删除，再插入新的数据
    //预测天气
    public void updateDataBaseForecast(List<weatherForecast.Daily_forecast> daily_forecasts){
        if (daily_forecasts==null)return;
        mSQLiteDatabase.delete(weatherDbSchema.forecastTable.NAME,null,null);
        for (int i=0;i<daily_forecasts.size();i++){
            ContentValues values = new ContentValues();
            weatherForecast.Daily_forecast day = daily_forecasts.get(i);
            values.put("id",i);
            values.put(weatherDbSchema.forecastTable.Cols.CON_CODE,day.getCond_code_d());
            values.put(weatherDbSchema.forecastTable.Cols.COND_TXT,day.getCond_txt_d());
            values.put(weatherDbSchema.forecastTable.Cols.DATE,day.getDate());
            values.put(weatherDbSchema.forecastTable.Cols.HUM,day.getHum());
            values.put(weatherDbSchema.forecastTable.Cols.PCPN,day.getPcpn());
            values.put(weatherDbSchema.forecastTable.Cols.POP,day.getPop());
            values.put(weatherDbSchema.forecastTable.Cols.PRES,day.getPres());
            values.put(weatherDbSchema.forecastTable.Cols.TMP_MAX,day.getTmp_max());
            values.put(weatherDbSchema.forecastTable.Cols.TMP_MIN,day.getTmp_min());
            values.put(weatherDbSchema.forecastTable.Cols.UV_INDEX,day.getUv_index());
            values.put(weatherDbSchema.forecastTable.Cols.VIS,day.getVis());
            values.put(weatherDbSchema.forecastTable.Cols.WIND_DIR,day.getWind_dir());
            values.put(weatherDbSchema.forecastTable.Cols.WIND_SC,day.getWind_sc());
            mSQLiteDatabase.insert(weatherDbSchema.forecastTable.NAME,null,values);
            Log.i(TAG, "updateDataBase: insert into table "+i);
        }
    }
    //实况天气
    public void updateDataBaseRealTime(weatherRealTime weather){
        if (weather==null)return;
        mSQLiteDatabase.delete(weatherDbSchema.realTimeTable.NAME,null,null);
        ContentValues values = new ContentValues();
        values.put(weatherDbSchema.realTimeTable.Cols.CLOUD,weather.getCloud());
        values.put(weatherDbSchema.realTimeTable.Cols.CON_CODE,weather.getCond_code());
        values.put(weatherDbSchema.realTimeTable.Cols.COND_TXT,weather.getCond_txt());
        values.put(weatherDbSchema.realTimeTable.Cols.HUM,weather.getHum());
        values.put(weatherDbSchema.realTimeTable.Cols.PCPN,weather.getPcpn());
        values.put(weatherDbSchema.realTimeTable.Cols.PRES,weather.getPres());
        values.put(weatherDbSchema.realTimeTable.Cols.TMP,weather.getTmp());
        values.put(weatherDbSchema.realTimeTable.Cols.VIS,weather.getVis());
        values.put(weatherDbSchema.realTimeTable.Cols.WIND_SC,weather.getWind_sc());
        values.put(weatherDbSchema.realTimeTable.Cols.WIND_DIR,weather.getWind_dir());
        mSQLiteDatabase.insert(weatherDbSchema.realTimeTable.NAME,null,values);

    }

    private class fetchForecastTask extends AsyncTask<Void, Void, List<weatherForecast.Daily_forecast>>{
        //后台线程获取天气预测值
        @Override
        protected List<weatherForecast.Daily_forecast> doInBackground(Void... voids) {
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
            if (daily_forecasts==null){
                Log.i(TAG, "onPostExecute: get foreacast weather fail");
            }
            //更新数据库
            updateDataBaseForecast(daily_forecasts);
            //将获得的数据绑定到mData中
            //mDatas.clear();
            if (daily_forecasts!=null){
                mDatas = daily_forecasts;
            }

            if (mDatas==null||mDatas.size()==0)return;
            mToday = mDatas.get(0);//获取当天的预测值
            if(MainActivity.isPhone){
                mDatas.remove(0);//去掉当天的天气
            }

            Log.i(TAG, "onPostExecute: size is "+mDatas.size());
            //数据获取完毕后通过Adapter显示上去
            if (mAdapter==null){
                setupAdapter();
            }
            else{
                mAdapter.notifyDataSetChanged();
            }
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
            if (weatherRealTime==null){
                Log.i(TAG, "onPostExecute: get weather realtime fail");
            }
            updateDataBaseRealTime(weatherRealTime);
            temperatrueNow.setText(celsiusToFahrenheit(weatherRealTime.getTmp())+tempUnit);
            tempToday = weatherRealTime.getTmp();
            locationNow.setText(sLocation);
            skyConNow.setText(weatherRealTime.getCond_txt());
            skyIcon.setBackgroundResource(myAdapter.getResId(weatherRealTime.getCond_code()));
        }
    }

    public static void setTitleBar(){
        ActionBar actionBar = MainActivity.sActionBar;
        actionBar.setTitle(R.string.app_name);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

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
        mDatas = new ArrayList<>();

        //开启线程监听位置是否改变
        new Thread(new fetchNewWeatherThread()).start();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what==1){
                    mAdapter.notifyDataSetChanged();
                    if (MainActivity.isPhone){
                        temperatrueNow.setText(celsiusToFahrenheit(tempToday)+tempUnit);
                    }
                }
            }
        };
        //打开数据库
        mContext = getContext();
        mSQLiteDatabase = new weatherDataBase(mContext).getWritableDatabase();
        //mDateReader = new weatherDataBase(mContext).getReadableDatabase();

    }

    //从数据库中读取数据并显示
    public void readData(){
        Cursor cursor = mSQLiteDatabase.rawQuery("select * from forecast",null);
        if (cursor!=null&&cursor.getCount()>0){
            weatherForecast forecast = new weatherForecast();
            while(cursor.moveToNext()){
                weatherForecast.Daily_forecast day = forecast.new Daily_forecast();
                day.setCond_code_d(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.CON_CODE)));
                day.setCond_txt_d(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.COND_TXT)));
                day.setDate(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.DATE)));
                day.setHum(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.HUM)));
                day.setPcpn(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.PCPN)));
                day.setPop(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.POP)));
                day.setPres(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.PRES)));
                day.setTmp_max(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.TMP_MAX)));
                day.setTmp_min(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.TMP_MIN)));
                day.setUv_index(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.UV_INDEX)));
                day.setVis(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.VIS)));
                day.setWind_sc(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.WIND_SC)));
                day.setWind_dir(cursor.getString(cursor.getColumnIndex(weatherDbSchema.forecastTable.Cols.WIND_DIR)));
                mDatas.add(day);
            }
        }
        if (mDatas.size()>0){
            setupAdapter();
            mToday = mDatas.get(0);
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
        if (MainActivity.isPhone){
            cursor = mSQLiteDatabase.rawQuery("select * from realTime",null);
            if (cursor!=null&&cursor.getCount()>0){
                final weatherRealTime weatherRealTime= new weatherRealTime();
                while(cursor.moveToNext()){
                    weatherRealTime.setCloud(cursor.getString(cursor.getColumnIndex(weatherDbSchema.realTimeTable.Cols.CLOUD)));
                    weatherRealTime.setCond_code(cursor.getString(cursor.getColumnIndex(weatherDbSchema.realTimeTable.Cols.CON_CODE)));
                    weatherRealTime.setCond_txt(cursor.getString(cursor.getColumnIndex(weatherDbSchema.realTimeTable.Cols.COND_TXT)));
                    weatherRealTime.setHum(cursor.getString(cursor.getColumnIndex(weatherDbSchema.realTimeTable.Cols.HUM)));
                    weatherRealTime.setPcpn(cursor.getString(cursor.getColumnIndex(weatherDbSchema.realTimeTable.Cols.PCPN)));
                    weatherRealTime.setPres(cursor.getString(cursor.getColumnIndex(weatherDbSchema.realTimeTable.Cols.PRES)));
                    weatherRealTime.setTmp(cursor.getString(cursor.getColumnIndex(weatherDbSchema.realTimeTable.Cols.TMP)));
                    weatherRealTime.setVis(cursor.getString(cursor.getColumnIndex(weatherDbSchema.realTimeTable.Cols.VIS)));
                    weatherRealTime.setWind_sc(cursor.getString(cursor.getColumnIndex(weatherDbSchema.realTimeTable.Cols.WIND_SC)));
                    weatherRealTime.setWind_dir(cursor.getString(cursor.getColumnIndex(weatherDbSchema.realTimeTable.Cols.WIND_DIR)));
                }

                temperatrueNow.setText(celsiusToFahrenheit(weatherRealTime.getTmp())+tempUnit);
                tempToday = weatherRealTime.getTmp();
                locationNow.setText(sLocation);
                skyConNow.setText(weatherRealTime.getCond_txt());
                skyIcon.setBackgroundResource(myAdapter.getResId(weatherRealTime.getCond_code()));

            }
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weather_list_fragment,container,false);
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

        readData();

        setTitleBar();

        //后台线程获取天气数据
        if (isNetworkOn(getContext())){
            new fetchForecastTask().execute();
            if(MainActivity.isPhone){
                new fetchNowTask().execute();
            }
        }


        return view;
    }

}
