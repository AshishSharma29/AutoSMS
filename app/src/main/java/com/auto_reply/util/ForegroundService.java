package com.auto_reply.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.auto_reply.MainActivity;
import com.auto_reply.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.auto_reply.calling.CallReceiver.sendLog;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "com.autoReply";

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, ForegroundService.class);
        intent.putExtra("inputExtra", "using call states in background");
        ContextCompat.startForegroundService(this, intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onTaskRemoved(intent);
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getApplicationContext().getResources().getString(R.string.app_name))
                .setContentText(input)
                .setSmallIcon(R.drawable.app_logo)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(2, notification);

        //do heavy work on a background thread
        //stopSelf();
        startTimer();
        return START_NOT_STICKY;
    }

    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
              //  Log.i("Count", "=========  "+ (counter++));
            }
        };
        timer.schedule(timerTask, 1000, 1000); //
    }

     public  void  StartNotification(Intent intent){
         String input = intent.getStringExtra("inputExtra");
         createNotificationChannel();
         Intent notificationIntent = new Intent(this, MainActivity.class);
         PendingIntent pendingIntent = PendingIntent.getActivity(this,
                 0, notificationIntent, 0);

         Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                 .setContentTitle(getApplicationContext().getResources().getString(R.string.app_name))
                 .setContentText(input)
                 .setSmallIcon(R.drawable.app_logo)
                 .setContentIntent(pendingIntent)
                 .build();
     }

     @Override
     public  void onTaskRemoved(Intent intent){
        try{
         ContextCompat.startForegroundService(this, intent);
         super.onTaskRemoved(intent);
            Date currentTime = Calendar.getInstance().getTime();
            sendLog(" onTaskRemoved  Called   "+currentTime);
        }
        catch (Exception en){
            sendLog(" onTaskRemoved  Exception   "+ en.getMessage()+" SDK Version "+Build.VERSION.SDK_INT);
        }
     }
    @Override
    public void onDestroy() {
        try{
        Intent intent = new Intent(this, ForegroundService.class);
        intent.putExtra("inputExtra", "using call states in background");
        ContextCompat.startForegroundService(this, intent);
        sendLog(" onDestroy  "+Build.VERSION.SDK_INT);
        }
        catch (Exception en){
            sendLog(" onDestroy  Exception   "+ en.getMessage()+" SDK Version "+Build.VERSION.SDK_INT);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}