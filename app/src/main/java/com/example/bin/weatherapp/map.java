package com.example.bin.weatherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;

public class map extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MapView mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        AMap aMap = mapView.getMap();
    }
}
