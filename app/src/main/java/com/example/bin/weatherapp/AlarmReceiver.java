package com.example.bin.weatherapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * @Author: Bhy
 * @Date: 2018/12/5
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final int NOTIFICATION_ID = 1000;
    private String NOTIFICATION_CHANNEL = "mychannel1";
    @Override
    public void onReceive(Context context, Intent intent) {
        //设置通知内容并在onReceive()这个函数执行时开启
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL);
        builder .setTicker("天气预报")
                .setSmallIcon(R.drawable.title_bar_icon)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle("小天气")
                        .setSummaryText("summaryText")
                        .addLine("1")
                        .addLine("2")
                        .addLine("3")
                        .addLine("4"))
                .setDefaults(Notification.DEFAULT_ALL);
        Notification notification = builder.build();
        manager.notify(1, notification);


        //再次开启LongRunningService这个服务，从而可以
        Intent i = new Intent(context, NotificationService.class);
        context.startService(i);

//        if (intent.getAction().equals("notification")){
//            new fetchWeatherTask().execute();
//            NotificationService.sendNotification(context);
//        }
    }
}
