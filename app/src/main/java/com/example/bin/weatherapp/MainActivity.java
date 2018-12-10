package com.example.bin.weatherapp;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.ant.liao.GifView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public AMapLocationClient mAMapLocationClient;
    public AMapLocationListener mAMapLocationListener;
    public AMapLocationClientOption mAMapLocationClientOption;
    private static final String TAG = "MainActivity";

    public static boolean isPhone = true;


    public static List<String> tags;

    public static ActionBar sActionBar;

    public static String cityNow = "长沙";
    public static Boolean isCelsius = true;
    public static Boolean isNoteOn = false;

    public static citys.City sCity ;

    private int REQUESTCODE = 1;

    public static boolean isChanged = false;


    public void setLocationSpiderConfig(){
        //初始化并绑定监听
        mAMapLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                //解析aMapLocation
                if (aMapLocation!=null){
                    if (aMapLocation.getErrorCode()==0){//等于0则成功定位
                        Log.i(TAG, "onLocationChanged: 定位成功\n"+"经纬度："+aMapLocation.getLongitude()+"."+aMapLocation.getLatitude()
                                                                        +"\n地址："+aMapLocation.getAddress());
                        weatherListFragment.location = aMapLocation.getLongitude()+","+aMapLocation.getLatitude();
                        weatherListFragment.sLocation = aMapLocation.getCity()+aMapLocation.getDistrict();
                        cityNow = aMapLocation.getCity();

                    }
                    else{
                        //定位失败，日志显示错误信息
                        Log.e(TAG, "onLocationChanged: "+aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                    }
                }
            }
        };

        mAMapLocationClient = new AMapLocationClient(getApplicationContext());
        mAMapLocationClient.setLocationListener(mAMapLocationListener);

        mAMapLocationClientOption = new AMapLocationClientOption();
        //高精度模式
        mAMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //单次定位
        mAMapLocationClientOption.setOnceLocation(true);

        //给定位客户端对象设置定位参数
        mAMapLocationClient.setLocationOption(mAMapLocationClientOption);
        //启动定位
        mAMapLocationClient.startLocation();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.title_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public static String getTag(){
        return tags.get(tags.size()-2);
    }

    public void shareWeather(){
        //隐式intent调用系统API进行分享
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, weatherListFragment.formatShareText());//extraText为文本的内容
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.map:
                //点击地图后跳转到地图对应的fragment
                Intent intentMap = new Intent(this,map.class);
                startActivity(intentMap);
                return true;
            case R.id.setting:{
                //Toast.makeText(this,"点击了setting",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,setting.class);
                intent.putExtra("city",cityNow);
                intent.putExtra("isCelsius",isCelsius);
                intent.putExtra("isNoteOn",isNoteOn);
                startActivityForResult(intent,REQUESTCODE);
                return true;
            }
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.share_detail:
                shareWeather();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: req code = "+requestCode+" res code = "+resultCode);
        if(resultCode==1&&requestCode==REQUESTCODE){
            Log.i(TAG, "onActivityResult: return MainActivity");
            Log.i(TAG, "onActivityResult: isCelsius = "+data.getBooleanExtra("isCelsius",false));

            if (isCelsius!=data.getBooleanExtra("isCelsius",true)){
                isCelsius = data.getBooleanExtra("isCelsius",true);
                if (!isCelsius){
                    weatherListFragment.tempUnit = "°F";
                }
                else{
                    weatherListFragment.tempUnit = "°C";
                }
                weatherListFragment.tempUnitChanged = true;
            }
            if (isNoteOn!=data.getBooleanExtra("isNoteOn",false)){
                isNoteOn = data.getBooleanExtra("isNoteOn",false);
                weatherListFragment.noteChanged = true;
                //注册
                Intent intent = new Intent(this, AlarmReceiver.class);
                PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
                // 过10s 执行
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 0);

                AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
                manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                Log.i(TAG, "onActivityResult: sent here");

            }

            String json = data.getStringExtra("city");
            if (json.equals("nothing"))return;
            sCity = new Gson().fromJson(json,citys.City.class);
            if (sCity.getLocation()!=cityNow){
                Log.i(TAG, "onActivityResult: fetch new weather");
                cityNow = sCity.getLocation();
                weatherListFragment.sLocation = cityNow;
                weatherListFragment.location = sCity.getLon()+","+sCity.getLat();
                weatherListFragment.needUpdate=true;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        tags = new ArrayList<>();

        sActionBar = getSupportActionBar();
        if (sActionBar!=null){
            sActionBar.setHomeButtonEnabled(false);
            sActionBar.setDisplayHomeAsUpEnabled(false);
//            sActionBar.getCustomView().findViewById(R.id.share_detail).setVisibility(View.INVISIBLE);
        }

        setLocationSpiderConfig();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        tags.add("listTag");
        if (fragment == null){
            fragment = new weatherListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container,fragment,"listTag")
                    .commit();
        }

        //判断是手机还是平板
        isPhone = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) < Configuration.SCREENLAYOUT_SIZE_LARGE;
        Log.i(TAG, "onCreate: isPhone is "+isPhone);
    }

    @Override
    protected void onDestroy() {
        //停止定位并销毁对象
        mAMapLocationClient.stopLocation();
        mAMapLocationClient.onDestroy();
        super.onDestroy();
    }
}
