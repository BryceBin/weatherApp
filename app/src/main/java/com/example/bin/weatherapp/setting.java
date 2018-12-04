package com.example.bin.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class setting extends AppCompatActivity {
    private TextView location;
    private TextView unit;
    private CheckBox cb;
    private TextView note;
    public Intent mIntent = new Intent();
    private boolean isChenged = false;
    private String newCity = "北京";
    private boolean isCelsius = true;
    private boolean isNoteOn = false;

    private static final String TAG = "setting";

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
                    isCelsius = true;
                }
                else{
                    unit.setText(R.string.temp_C);
                    isCelsius = false;
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

        bindViews();
        
        setListeners();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==2&&requestCode==2){
            //解析data
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mIntent.putExtra("city",newCity);
        mIntent.putExtra("isCelsius",isCelsius);
        mIntent.putExtra("isNoteOn",isNoteOn);
        setResult(1,mIntent);
    }
}
