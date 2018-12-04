package com.example.bin.weatherapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.google.gson.Gson;

public class setting extends AppCompatActivity {
    private TextView location;
    private TextView unit;
    private CheckBox cb;
    private TextView note;
    public Intent mIntent = new Intent();
    private boolean isCelsius = true;
    private boolean isNoteOn = false;
    private citys.City selectedCity = null;

    private static final String TAG = "setting";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void bindViews(){
        location = findViewById(R.id.location_setting);
        unit = findViewById(R.id.temp_unit);
        cb = findViewById(R.id.note_switch_checkBox);
        note = findViewById(R.id.note_switch);
    }

    public void setListeners(){
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: click location");
                //todo
                Intent intent = new Intent(setting.this,searchCity.class);
                startActivityForResult(intent,2);
            }
        });

        unit.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: click unit");
                if (unit.getText().equals("摄氏度")){
                    unit.setText(R.string.temp_F);
                    isCelsius = false;
                }
                else{
                    unit.setText(R.string.temp_C);
                    isCelsius = true;
                }
            }
        });

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i(TAG, "onCheckedChanged: click checkbox");
                if (b){
                    note.setText(R.string.on);
                    isNoteOn = true;
                }
                else{
                    note.setText(R.string.off);
                    isNoteOn = false;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle(R.string.setting);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        bindViews();
        
        setListeners();

        Intent intent = getIntent();
        location.setText(intent.getStringExtra("city"));
        if (!intent.getBooleanExtra("isCelsius",true)){
            unit.setText(R.string.temp_F);
        }
        if (intent.getBooleanExtra("isNoteOn",false)){
            cb.setChecked(true);
            note.setText(R.string.on);
        }

        mIntent.putExtra("city","nothing");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: req code = "+requestCode+"res code = "+resultCode);
        if (resultCode==2&&requestCode==2){
            //解析data
            Log.i(TAG, "onActivityResult: get resCity here");
            String json = data.getStringExtra("resCity");
            selectedCity = new Gson().fromJson(json,citys.City.class);
            if (selectedCity==null)return;
            location.setText(selectedCity.getLocation());
        }
    }

    @Override
    public void finish() {
        if (selectedCity!=null){
            mIntent.putExtra("city",new Gson().toJson(selectedCity));
        }
        mIntent.putExtra("isCelsius",isCelsius);
        mIntent.putExtra("isNoteOn",isNoteOn);
        setResult(1,mIntent);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (selectedCity!=null){
            mIntent.putExtra("city",new Gson().toJson(selectedCity));
        }
        mIntent.putExtra("isCelsius",isCelsius);
        mIntent.putExtra("isNoteOn",isNoteOn);
        setResult(1,mIntent);
        Log.i(TAG, "onBackPressed: return mainactivity with intent");
        super.onBackPressed();
    }

}
