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

import static com.example.bin.weatherapp.weatherListFragment.tempUnit;

public class NotificationService extends Service {
    public static weatherForecast.Daily_forecast mDaily_forecast;
    public static Notification mNotification;
    public static NotificationCompat.Builder sBuilder;
    public static boolean isDone;

    private static final String NOTIFICATION_CHANNEL = "myChannel1";
    public NotificationService() {
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
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int Minutes = 5*1000;
        //SystemClock.elapsedRealtime()表示1970年1月1日0点至今所经历的时间
        long triggerAtTime = SystemClock.elapsedRealtime() + Minutes;
        //此处设置开启AlarmReceiver这个Service
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        //ELAPSED_REALTIME_WAKEUP表示让定时任务的出发时间从系统开机算起，并且会唤醒CPU。
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
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
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.cancel(pi);
        super.onDestroy();
    }
}

class fetchWeatherTask extends AsyncTask<Void,Void, weatherForecast.Daily_forecast>{
    @Override
    protected weatherForecast.Daily_forecast doInBackground(Void... voids) {
        NotificationService.mDaily_forecast = weatherSpider.getTodayForecast(weatherListFragment.location);
        return NotificationService.mDaily_forecast;
    }

    @Override
    protected void onPostExecute(weatherForecast.Daily_forecast daily_forecast) {
        weatherForecast.Daily_forecast today = daily_forecast;
        NotificationService.sBuilder
                .setSmallIcon(R.drawable.title_bar_icon)
                .setTicker("今日天气")
                .setContentTitle("小天气")
                .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle("小天气")
                        .setSummaryText("summaryText")
                        .addLine(String.format("气温:%s%s~%s%s",weatherListFragment.celsiusToFahrenheit(today.getTmp_min()),tempUnit,weatherListFragment.celsiusToFahrenheit(today.getTmp_max()),tempUnit))
                        .addLine(String.format("降水量:%s",today.getPcpn()))
                        .addLine(String.format("紫外线强度:%s",today.getUv_index()))
                        .addLine(String.format("%s%s",today.getWind_dir(),today.getWind_sc())))
                .setDefaults(Notification.DEFAULT_ALL);
        //NotificationService.mNotification = NotificationService.sBuilder.build();
    }
}
