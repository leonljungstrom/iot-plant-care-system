package com.example.wateringapp.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.wateringapp.settingspreferences.SettingsPreferences;

import java.util.Calendar;

public class RescheduleService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SettingsPreferences prefs = new SettingsPreferences(this);

        if (prefs.isIntervalModeEnabled()) {
            long intervalMillis = prefs.getIntervalDays() * 24 * 60 * 60 * 1000L;

            Calendar startDate = Calendar.getInstance();
            startDate.set(Calendar.YEAR, prefs.getCalendarYear());
            startDate.set(Calendar.MONTH, prefs.getCalendarMonth());
            startDate.set(Calendar.DAY_OF_MONTH, prefs.getCalendarDay());
            startDate.set(Calendar.HOUR_OF_DAY, prefs.getIntervalHour());
            startDate.set(Calendar.MINUTE, prefs.getIntervalMinute());
            startDate.set(Calendar.SECOND, 0);

            long triggerTime = startDate.getTimeInMillis();

            if (triggerTime < System.currentTimeMillis()) {
                while (triggerTime < System.currentTimeMillis()) {
                    triggerTime += intervalMillis;
                }
            }

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }

        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

