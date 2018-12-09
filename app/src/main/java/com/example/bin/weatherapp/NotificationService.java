package com.example.bin.weatherapp;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.bin.weatherapp.weatherListFragment.tempUnit;

public class NotificationService extends Service {
    public static weatherForecast.Daily_forecast mDaily_forecast;
    public static Notification mNotification;
    public static NotificationCompat.Builder sBuilder;
    private static Timer sTimer = null;
    public static boolean isDone;

    private static final String TAG = "NotificationService";

    private static final String NOTIFICATION_CHANNEL = "myChannel1";
    public NotificationService() {
    }

    //清除通知
    public static void clearNotifications(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        manager.cancelAll();
        if (sTimer!=null){
            sTimer.cancel();
            sTimer = null;
        }
    }
    //添加通知
    public static void addNotification(Context context,int delay,String tickerText, String contentTitle, String contextText){
        Intent intent = new Intent(context,NotificationService.class);
        intent.putExtra("delay",delay);
        intent.putExtra("ticketText",tickerText);
        intent.putExtra("contextTitle",contentTitle);
        intent.putExtra("contextText",contextText);
        Log.i(TAG, "addNotification: 01");
        context.startService(intent);
        Log.i(TAG, "addNotification: 02");
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void sendNotification(Context context){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT>=26){
            //API26以上必须自定义NotificationChannel
            notificationManager.createNotificationChannel(weatherListFragment.genChannel(NOTIFICATION_CHANNEL));

        }
        Intent intent1 = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,0);
        sBuilder = new NotificationCompat.Builder(context);
        if (sBuilder!=null){
            sBuilder.setContentIntent(pendingIntent);
            mNotification = sBuilder.build();
            notificationManager.notify(1,mNotification);
            isDone = true;
        }
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: 002");
        long period = 60*60*24*1000;
        int delay = intent.getIntExtra("delay",0);
        if (sTimer==null){
            sTimer = new Timer();
        }
        sTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "run: 1");
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(NotificationService.this);
                Intent notificationIntent = new Intent(NotificationService.this,MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this,0,notificationIntent,0);
                Log.i(TAG, "run: 2");
                builder.setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.title_bar_icon)
                        .setTicker(intent.getStringExtra("ticketText"))
                        .setContentTitle(intent.getStringExtra("contextTitle"))
                        .setContentText(intent.getStringExtra("contextText"))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL);
                Log.i(TAG, "run: 3");

                manager.notify((int)System.currentTimeMillis(),builder.build());
                Log.i(TAG, "run: 4");


            }
        },delay,0);
//
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        int Minutes = 5*1000;
//        //SystemClock.elapsedRealtime()表示1970年1月1日0点至今所经历的时间
//        long triggerAtTime = SystemClock.elapsedRealtime() + Minutes;
//        //此处设置开启AlarmReceiver这个Service
//        Intent i = new Intent(this, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        //ELAPSED_REALTIME_WAKEUP表示让定时任务的出发时间从系统开机算起，并且会唤醒CPU。
//        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
//        mNotification = null;
//        isDone = false;
//        new fetchWeatherTask().execute();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    if (!isDone){
//                        sendNotification(getApplication());
//                        isDone = true;
//                        break;
//                    }
//                }
//
//            }
//        }).start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //在Service结束后关闭AlarmManager
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent i = new Intent(this, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        manager.cancel(pi);
        super.onDestroy();
    }
}

