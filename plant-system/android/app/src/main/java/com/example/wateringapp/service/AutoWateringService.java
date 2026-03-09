package com.example.wateringapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.wateringapp.MqttManager.MqttManager;
import com.example.wateringapp.R;
import com.example.wateringapp.alarm.AlarmReceiver;
import com.example.wateringapp.alarm.AlarmScheduler;

public class AutoWateringService extends Service {

    private static final String CHANNEL_ID = "AutoWateringChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Watering Plants")
                .setContentText("Sending MQTT watering command...")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wateringapp::mqttlock");
        wakeLock.acquire(10 * 1000L);

        MqttManager mqtt = MqttManager.getInstance();
        mqtt.connectWithCallback(() -> {
            mqtt.publish("plants/watering", "start");
            AlarmScheduler.scheduleNextIntervalAlarm(getApplicationContext());
            wakeLock.release();
            stopSelf();
        });

        return START_NOT_STICKY;
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
                    "Auto Watering Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
