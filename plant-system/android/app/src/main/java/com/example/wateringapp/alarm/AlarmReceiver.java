package com.example.wateringapp.alarm;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.wateringapp.MqttManager.MqttManager;
import com.example.wateringapp.service.AutoWateringService;
import com.example.wateringapp.settingspreferences.SettingsPreferences;
import com.example.wateringapp.wateringlog.WateringEventLogger;


public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Watering triggered automatically!", Toast.LENGTH_SHORT).show();

        Intent serviceIntent = new Intent(context, AutoWateringService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }


}

