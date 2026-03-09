package com.example.wateringapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import com.example.wateringapp.Notifictations.ActivityNotif;
import com.example.wateringapp.R;
import com.example.wateringapp.MqttManager.MqttManager;
import com.example.wateringapp.settingspreferences.SettingsPreferences;
import com.example.wateringapp.ui.mainmenu.MainMenuFragment;
import com.example.wateringapp.viewmodel.MqttViewModel;
import com.example.wateringapp.viewmodel.WateringLogViewModel;
import com.example.wateringapp.wateringlog.WateringEventLogger;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIF_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MqttViewModel mqttViewModel = new ViewModelProvider(this).get(MqttViewModel.class);
        WateringLogViewModel logViewModel = new ViewModelProvider(this).get(WateringLogViewModel.class);


        // Attach global watering logger ONCE here
        WateringEventLogger.attach(this, this, mqttViewModel, logViewModel);

        SettingsPreferences prefs = new SettingsPreferences(this);

        mqttViewModel.getSoilMoisture().observe(this, moisture -> {
            Log.d("WATER_LOGS", "Observed moisture: " + moisture);

            // Only auto-water if threshold mode is enabled (not interval) and moisture is low
            if (prefs.isAutoWateringEnabled()
                    && !prefs.isIntervalModeEnabled()
                    && moisture < prefs.getMoistureThreshold()) {
                Log.d("WATER_LOGS", "Auto-watering condition met. Publishing...");
                mqttViewModel.publishWithMode("plants/watering", "start", "auto");
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{"android.permission.POST_NOTIFICATIONS"},
                        1001);
            }
        }

        // Ask for permission to whitelist app from battery saving to allow for mqtt to run in background
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(android.net.Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }



        // Load Main Menu as the initial screen
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container_view, new MainMenuFragment());
            transaction.commit();
        }


    }
}

