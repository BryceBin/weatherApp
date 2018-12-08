package com.example.bin.weatherapp;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;

public class map extends AppCompatActivity {
    private static final String TAG = "map";
    private UiSettings mUiSettings;

    public void setTitleBar(){
        ActionBar actionBar = MainActivity.sActionBar;
        actionBar.setTitle(R.string.map);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //setTitleBar();



        MapView mapView = findViewById(R.id.map_view);
        Log.i(TAG, "onCreate: savedInstanceState is null? "+savedInstanceState==null?"true":"false");
        mapView.onCreate(savedInstanceState);
        AMap aMap = mapView.getMap();

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.interval(2000);

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setMyLocationEnabled(true);


        //连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);
        myLocationStyle.showMyLocation(true);

        //获取UI控件对象
        mUiSettings = aMap.getUiSettings();
        //显示指南针
        mUiSettings.setCompassEnabled(true);
        //显示比例尺
        mUiSettings.setScaleControlsEnabled(true);

        //设置默认比例尺为100m
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));



        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.i(TAG, "onMyLocationChange: "+location.getLatitude());
            }
        });
    }



}
