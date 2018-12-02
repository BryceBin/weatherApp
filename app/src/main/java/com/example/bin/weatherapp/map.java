package com.example.bin.weatherapp;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;

public class map extends AppCompatActivity {
    private static final String TAG = "map";
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapView mapView = findViewById(R.id.map_view);
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
