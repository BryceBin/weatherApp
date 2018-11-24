package com.example.bin.weatherapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @Author: Bhy
 * @Date: 2018/11/23
 */
public class SoundActivity extends AppCompatActivity {
    private static final String TAG = "SoundActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: SoundActivity started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.sound_fragment);
        if (fragment == null) {
            fragment = new SoundFragment();
            fm.beginTransaction()
                    .add(R.id.sound_fragment, fragment)
                    .commit();
        }
    }
}
