package com.example.bin.weatherapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

//import static com.example.bin.weatherapp.weatherListFragment.*;

/**
 * @Author: Bhy
 * @Date: 2018/12/5
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1000;
    private String NOTIFICATION_CHANNEL = "mychannel1";
    NotificationManager manager;
    public static NotificationCompat.Builder builder;
    public static weatherForecast.Daily_forecast today;
    public static boolean flag = false;
    private static final String TAG = "AlarmReceiver";

    public void addNotification(PendingIntent intent){
        builder .setTicker("天气预报")
                .setSmallIcon(R.drawable.title_bar_icon)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(intent)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle("小天气")
                        .addLine(String.format("气温:%s%s~%s%s",weatherListFragment.celsiusToFahrenheit(today.getTmp_min()),weatherListFragment.tempUnit,weatherListFragment.celsiusToFahrenheit(today.getTmp_max()),weatherListFragment.tempUnit))
                        .addLine(String.format("降水量:%s",today.getPcpn()))
                        .addLine(String.format("紫外线强度:%s",today.getUv_index()))
                        .addLine(String.format("%s%s",today.getWind_dir(),today.getWind_sc())))
                .setDefaults(Notification.DEFAULT_ALL);
        Notification notification = builder.build();
        manager.notify(1, notification);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //设置通知内容并在onReceive()这个函数执行时开启
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Toast.makeText(context,"toast",Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT>=26){
            //API26以上必须自定义NotificationChannel
            manager.createNotificationChannel(weatherListFragment.genChannel(NOTIFICATION_CHANNEL));
        }
        builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL);
        today = weatherListFragment.mToday;
        Intent intent1 = new Intent(context,MainActivity.class);
        Log.i(TAG, "onReceive: receive broadcast");
        final PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,0);
        if (today!=null){
            Log.i(TAG, "onReceive: today is not null");
            addNotification(pendingIntent);
        }
        else{
            new fetchWeatherTask().execute();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        if (flag){
                            Log.i(TAG, "run: got new today");
                            addNotification(pendingIntent);
                            flag=false;
                            break;
                        }
                    }
                }
            }).start();
        }
    }
}


class fetchWeatherTask extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "fetchWeatherTask";
    @Override
    protected Void doInBackground(Void... voids) {
        AlarmReceiver.today = weatherSpider.getTodayForecast(weatherListFragment.location);
        AlarmReceiver.flag = true;
        Log.i(TAG, "doInBackground: get today");
        return null;
    }

}

