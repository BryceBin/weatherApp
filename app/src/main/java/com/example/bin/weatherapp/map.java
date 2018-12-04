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

public class map extends Fragment {
    private static final String TAG = "map";
    private UiSettings mUiSettings;
    private String lastTag;

    public void setTitleBar(){
        ActionBar actionBar = MainActivity.sActionBar;
        actionBar.setTitle(R.string.map);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static map newInstance(String json){
        map mMap = new map();
        Bundle args = new Bundle();
        args.putString("data",json);
        mMap.setArguments(args);
        return mMap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_map,container,false);

        setTitleBar();

        Fragment fragment = getFragmentManager().findFragmentByTag(MainActivity.getTag());
        getFragmentManager().beginTransaction().hide(fragment).commit();

        MapView mapView = view.findViewById(R.id.map_view);
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

        return view;
    }

    @Override
    public void onDestroyView() {
        Fragment fragment = getFragmentManager().findFragmentByTag(MainActivity.getTag());
        MainActivity.tags.remove(MainActivity.tags.size()-1);
        weatherListFragment.setTitleBar();
        getFragmentManager().beginTransaction().show(fragment).commit();
        super.onDestroyView();
    }
}
