package com.example.bin.weatherapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.ant.liao.GifView;

public class MainActivity extends AppCompatActivity {
    public AMapLocationClient mAMapLocationClient;
    public AMapLocationListener mAMapLocationListener;
    public AMapLocationClientOption mAMapLocationClientOption;
    private static final String TAG = "MainActivity";


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

                    }
                    else{
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.map:
                startActivity(new Intent(MainActivity.this, map.class));
                return true;
            case R.id.setting:
                Toast.makeText(this,"点击了setting",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLocationSpiderConfig();

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = new weatherListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container,fragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapLocationClient.stopLocation();
        mAMapLocationClient.onDestroy();
    }
}
