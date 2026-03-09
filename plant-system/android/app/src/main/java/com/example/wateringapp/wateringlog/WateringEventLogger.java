package com.example.wateringapp.wateringlog;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.wateringapp.settingspreferences.SettingsPreferences;
import com.example.wateringapp.viewmodel.MqttViewModel;
import com.example.wateringapp.viewmodel.WateringLogViewModel;
import com.example.wateringapp.wateringlog.WateringLog;

public class WateringEventLogger {

    // Prevent double logging from MQTT + manual trigger
    private static long lastLogTime = 0;

    public static void attach(Context context, LifecycleOwner lifecycleOwner,
                              MqttViewModel mqttViewModel,
                              WateringLogViewModel logViewModel) {

        SettingsPreferences prefs = new SettingsPreferences(context);

        mqttViewModel.getWateringStarted().observe(lifecycleOwner, mode -> {
            long now = System.currentTimeMillis();

            // Block if last log was within 2 seconds
            if (now - lastLogTime < 1000) {
                Log.w("WATER_LOGS", "Duplicate watering log blocked (" + mode + ")");
                return;
            }

            lastLogTime = now;

            WateringLog log = new WateringLog();
            log.timestamp = now;
            log.amount = prefs.getWaterAmount();
            log.mode = mode;
            logViewModel.addLog(log);

            Log.d("WATER_LOGS", "[Global] Log saved: " + log.amount + "ml " + log.mode);
        });
    }

    public static void logBackgroundWatering(Context context, String mode) {
        SettingsPreferences prefs = new SettingsPreferences(context);
        WateringLog log = new WateringLog();
        log.timestamp = System.currentTimeMillis();
        log.amount = prefs.getWaterAmount();
        log.mode = mode;

        new Thread(() -> {
            WateringLogDatabase.getInstance(context)
                    .wateringLogDao()
                    .insert(log);
            Log.d("WATER_LOGS", "[Background] Log saved: " + log.amount + "ml " + log.mode);
        }).start();
    }
}

