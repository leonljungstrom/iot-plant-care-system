package com.example.wateringapp.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.wateringapp.settingspreferences.SettingsPreferences;

public class AlarmScheduler {

    public static void scheduleNextIntervalAlarm(Context context) {
        SettingsPreferences prefs = new SettingsPreferences(context);

        if (!prefs.isIntervalModeEnabled()) return;

        long intervalMillis = prefs.getIntervalMillis();
        long nextTriggerTime = System.currentTimeMillis() + intervalMillis;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerTime, pendingIntent);
    }
}
