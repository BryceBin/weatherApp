package com.example.bin.weatherapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * @Author: Bhy
 * @Date: 2018/11/23
 */
public class DisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.display_fragment);
        if(fragment == null){
            fragment = new DisplayFragment();
            fm.beginTransaction()
                    .add(R.id.display_fragment,fragment)
                    .commit();
        }
    }
}
