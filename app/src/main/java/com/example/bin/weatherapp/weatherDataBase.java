package com.example.bin.weatherapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @Author: Bhy
 * @Date: 2018/12/9
 */
public class weatherDataBase extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "weatherBase.db";

    public weatherDataBase(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //建表
        sqLiteDatabase.execSQL("create table "+ weatherDbSchema.forecastTable.NAME+"("+
                                weatherDbSchema.forecastTable.Cols.ID+","+
                                weatherDbSchema.forecastTable.Cols.CON_CODE+","+
                                weatherDbSchema.forecastTable.Cols.COND_TXT+","+
                                weatherDbSchema.forecastTable.Cols.DATE+","+
                                weatherDbSchema.forecastTable.Cols.HUM+","+
                                weatherDbSchema.forecastTable.Cols.PCPN+","+
                                weatherDbSchema.forecastTable.Cols.POP+","+
                                weatherDbSchema.forecastTable.Cols.PRES+","+
                                weatherDbSchema.forecastTable.Cols.TMP_MAX+","+
                                weatherDbSchema.forecastTable.Cols.TMP_MIN+","+
                                weatherDbSchema.forecastTable.Cols.UV_INDEX+","+
                                weatherDbSchema.forecastTable.Cols.VIS+","+
                                weatherDbSchema.forecastTable.Cols.WIND_DIR+","+
                                weatherDbSchema.forecastTable.Cols.WIND_SC+
                                ")");

        sqLiteDatabase.execSQL("create table "+weatherDbSchema.realTimeTable.NAME+"("+
                                weatherDbSchema.realTimeTable.Cols.CLOUD+","+
                                weatherDbSchema.realTimeTable.Cols.CON_CODE+","+
                                weatherDbSchema.realTimeTable.Cols.COND_TXT+","+
                                weatherDbSchema.realTimeTable.Cols.HUM+","+
                                weatherDbSchema.realTimeTable.Cols.PCPN+","+
                                weatherDbSchema.realTimeTable.Cols.PRES+","+
                                weatherDbSchema.realTimeTable.Cols.TMP+","+
                                weatherDbSchema.realTimeTable.Cols.VIS+","+
                                weatherDbSchema.realTimeTable.Cols.WIND_SC+","+
                                weatherDbSchema.realTimeTable.Cols.WIND_DIR+
                        ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {


    }
}
