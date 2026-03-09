package com.example.wateringapp.ui.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Locale;

import com.example.wateringapp.Notifictations.ActivityNotif;
import com.example.wateringapp.R;
import com.example.wateringapp.ui.waterReservoir.waterReservoirManager;
import com.example.wateringapp.settingspreferences.SettingsPreferences;
import com.example.wateringapp.viewmodel.MqttViewModel;
import com.example.wateringapp.viewmodel.WateringLogViewModel;
import com.example.wateringapp.wateringlog.WateringEventLogger;
import com.example.wateringapp.wateringlog.WateringLog;
import com.example.wateringapp.wateringlog.WateringLogAdapter;
import com.example.wateringapp.alarm.AlarmReceiver;

public class WateringSettingsFragment extends Fragment {

    private MqttViewModel viewModel;
    private SettingsPreferences prefs;

    // UI references stored as fields so we can reuse them
    private ImageButton schedulingButton, intervalButton, moistureThresholdButton,waterButton;

    private LinearLayout layoutThreshold, layoutInterval, layoutDayInterval;
    private TextView scheduledTimeText, selectedDateText, intervalDaysText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_watering_settings, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(MqttViewModel.class);

        //manages user preferences like threshold, amount
        prefs = new SettingsPreferences(requireContext());

        //watering logging instances
        WateringLogViewModel logViewModel = new ViewModelProvider(requireActivity()).get(WateringLogViewModel.class);
        RecyclerView logRecycler = view.findViewById(R.id.recycler_logs);
        WateringLogAdapter logAdapter = new WateringLogAdapter();


        // Setup for log history RecyclerView (shows last 10 waterings)
        logRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        logRecycler.setAdapter(logAdapter);

        logViewModel.getLast10Logs().observe(getViewLifecycleOwner(), logs -> {
            Log.d("WATER_LOGS", "Fetched logs: " + logs.size());
            logAdapter.setLogs(logs);
        });

        // references
        SeekBar moistureSlider = view.findViewById(R.id.seek_moisture);
        TextView moistureLabel = view.findViewById(R.id.text_moisture);
        TextView moistureStatus = view.findViewById(R.id.text_moisture_status);
        TextView waterLevel = view.findViewById(R.id.text_water_level);
        SeekBar reservoirHeightSlider = view.findViewById(R.id.seek_reservoir_height);
        TextView heightLabel = view.findViewById(R.id.text_reservoir_height);
        SeekBar amountSlider = view.findViewById(R.id.seek_amount);
        TextView amountLabel = view.findViewById(R.id.text_amount);
        Button showLogsButton = view.findViewById(R.id.btn_show_logs);
        LinearLayout logsContainer = view.findViewById(R.id.logs_container);
        Switch autoSwitch = view.findViewById(R.id.switch_auto);
        Switch notificationSwitch = view.findViewById(R.id.switch_notification);

        // Store these as fields so we can use in update method
        schedulingButton = view.findViewById(R.id.btn_scheduling);
        intervalButton = view.findViewById(R.id.btn_interval);
        moistureThresholdButton = view.findViewById(R.id.btn_threshold);
        waterButton = view.findViewById(R.id.btn_water);
        selectedDateText = view.findViewById(R.id.text_selected_date);
        scheduledTimeText = view.findViewById(R.id.text_scheduled_time);
        intervalDaysText = view.findViewById(R.id.text_selected_interval_days);

        layoutThreshold = view.findViewById(R.id.layout_threshold);
        layoutInterval = view.findViewById(R.id.layout_interval);
        layoutDayInterval = view.findViewById(R.id.layout_day_interval);

        Button selectDateButton = view.findViewById(R.id.btn_select_date);
        Button intervalDaysButton = view.findViewById(R.id.btn_select_interval_days);

        // Restore saved values
        moistureSlider.setProgress(prefs.getMoistureThreshold());
        amountSlider.setProgress(prefs.getWaterAmount());
        autoSwitch.setChecked(prefs.isAutoWateringEnabled());
        notificationSwitch.setChecked(prefs.isNotificationsEnabled());

        moistureLabel.setText("Moisture Threshold: " + prefs.getMoistureThreshold() + "%");
        amountLabel.setText("Watering Amount: " + prefs.getWaterAmount() + "ml");

        // Set up click listeners
        moistureThresholdButton.setOnClickListener(v -> {
            prefs.setIntervalModeEnabled(false);
            updateWateringModeUI();
            cancelIntervalAlarm();
        });

        intervalButton.setOnClickListener(v -> {
            prefs.setIntervalModeEnabled(true);
            resetSchedulingTime();
            updateWateringModeUI();
            cancelIntervalAlarm();
            scheduleNextIntervalAlarm();
        });

        schedulingButton.setOnClickListener(v -> {
            if (prefs.isIntervalModeEnabled()) {
                ClockFragment timePicker = new ClockFragment();
                timePicker.setInitialTime(prefs.getIntervalHour(), prefs.getIntervalMinute());

                timePicker.setOnTimeSelectedListener((hour, minute) -> {
                    Calendar nowCal = Calendar.getInstance();
                    Calendar pickCal = Calendar.getInstance();
                    pickCal.set(Calendar.YEAR, prefs.getCalendarYear());
                    pickCal.set(Calendar.MONTH, prefs.getCalendarMonth());
                    pickCal.set(Calendar.DAY_OF_MONTH, prefs.getCalendarDay());
                    pickCal.set(Calendar.HOUR_OF_DAY, hour);
                    pickCal.set(Calendar.MINUTE, minute);
                    pickCal.set(Calendar.SECOND, 0);

                    boolean sameDay = nowCal.get(Calendar.YEAR) == pickCal.get(Calendar.YEAR)
                            && nowCal.get(Calendar.DAY_OF_YEAR) == pickCal.get(Calendar.DAY_OF_YEAR);
                    if (sameDay && pickCal.before(nowCal)) {
                        Toast.makeText(requireContext(),
                                "Please choose a time in the future", Toast.LENGTH_SHORT).show();
                        schedulingButton.performClick();
                        return;
                    }

                    prefs.setIntervalHour(hour);
                    prefs.setIntervalMinute(minute);
                    cancelIntervalAlarm();
                    scheduleNextIntervalAlarm();

                    scheduledTimeText.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                });

                timePicker.show(getParentFragmentManager(), "timePicker");
            }
        });

        selectDateButton.setOnClickListener(v -> {
            CalendarFragment calendarFragment = new CalendarFragment();
            calendarFragment.setInitialDate(prefs.getCalendarYear(), prefs.getCalendarMonth(), prefs.getCalendarDay());

            calendarFragment.setOnDateSelectedListener(date -> {
                selectedDateText.setText(date);
                prefs.setCalendarDay(Integer.parseInt(date.split("-")[2]));
                prefs.setCalendarMonth(Integer.parseInt(date.split("-")[1]) - 1);
                prefs.setCalendarYear(Integer.parseInt(date.split("-")[0]));
                cancelIntervalAlarm();
                scheduleNextIntervalAlarm();
            });

            calendarFragment.show(getParentFragmentManager(), "calendarPicker");
        });

        intervalDaysText.setText(prefs.getIntervalDays() + " days");

        intervalDaysButton.setOnClickListener(v -> {
            NumberPickerDialogFragment dialog = new NumberPickerDialogFragment();
            dialog.setInitialValue(prefs.getIntervalDays());

            dialog.setOnNumberSelectedListener(number -> {
                prefs.setIntervalDays(number);
                intervalDaysText.setText(number + " days");
                cancelIntervalAlarm();
                scheduleNextIntervalAlarm();
            });

            dialog.show(getParentFragmentManager(), "intervalPicker");
        });

        // Slider logic
        moistureSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value;
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                moistureLabel.setText("Moisture Threshold: " + progress + "%");
            }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                prefs.setMoistureThreshold(value);
                viewModel.publish("plants/waterPercentage", String.valueOf(value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        });

        amountSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value;
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                amountLabel.setText("Watering Amount: " + progress + "ml");
            }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                prefs.setWaterAmount(value);
                viewModel.publish("plants/wateringAmount", String.valueOf(value));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        });

        reservoirHeightSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value;
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                heightLabel.setText("Reservoir Height: " + value + "cm");
            }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                prefs.setReservoirHeight(value);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
        });

        waterButton.setOnClickListener(v -> viewModel.publishWithMode("plants/watering", "start", "manual"));
        autoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.setAutoWateringEnabled(isChecked));
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> prefs.setNotificationsEnabled(isChecked));


        // Toggle: Notification
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.setNotificationsEnabled(isChecked)  // Save using SettingsPreferences
        );

        // Using viewmodel to get values
        viewModel.getSoilMoisture().observe(getViewLifecycleOwner(), moisture -> {
            moistureStatus.setText("Soil Moisture: " + moisture + "%");
            if (moisture < prefs.getMoistureThreshold()) {
                checkWatering();  // All notification and cooldown logic moved inside this method
            }
        });





        showLogsButton.setOnClickListener(v -> {
            if (logsContainer.getVisibility() == View.GONE) {
                logsContainer.setVisibility(View.VISIBLE);
                showLogsButton.setText("Hide Logs");
            } else {
                logsContainer.setVisibility(View.GONE);
                showLogsButton.setText("Show Logs");
            }
        });

        viewModel.getWaterReservoirLevel().observe(getViewLifecycleOwner(), waterDistance -> {
            int waterPercentage = waterReservoirManager.calculateWaterPercentage(prefs.getReservoirHeight(), waterDistance);
            waterLevel.setText("Reservoir Level: " + waterPercentage+ "%");
            if (waterPercentage < 15 && prefs.isNotificationsEnabled()) {
                long now = System.currentTimeMillis();
                long last = prefs.getLastReservoirNotificationTime();
                if (now - last >= 60_000) { // 1-minute cooldown
                    waterReservoirManager.reservoirNotify(waterPercentage, requireContext());
                    prefs.setLastReservoirNotificationTime(now);
                }
            }

        });






        // 🔧 FIX: Restore UI mode based on saved preference
        updateWateringModeUI();

        return view;
    }

    private void updateWateringModeUI() {
        boolean isIntervalMode = prefs.isIntervalModeEnabled();

        if (isIntervalMode) {
            moistureThresholdButton.setAlpha(0.5f);
            intervalButton.setAlpha(1.0f);
            schedulingButton.setAlpha(1.0f);
            layoutThreshold.setVisibility(View.GONE);
            layoutInterval.setVisibility(View.VISIBLE);
            layoutDayInterval.setVisibility(View.VISIBLE);

            selectedDateText.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    prefs.getCalendarYear(), prefs.getCalendarMonth() + 1, prefs.getCalendarDay()));
            scheduledTimeText.setText(String.format(Locale.getDefault(), "%02d:%02d",
                    prefs.getIntervalHour(), prefs.getIntervalMinute()));
            intervalDaysText.setText(prefs.getIntervalDays() + " days");

        } else {
            moistureThresholdButton.setAlpha(1.0f);
            intervalButton.setAlpha(0.5f);
            schedulingButton.setAlpha(0.5f);
            layoutThreshold.setVisibility(View.VISIBLE);
            layoutInterval.setVisibility(View.GONE);
            layoutDayInterval.setVisibility(View.GONE);
        }
    }

    private void checkWatering() {
        long now = System.currentTimeMillis();
        if (now - prefs.getLastMoistureNotificationTime() < 60_000) {

            return;
        }

        prefs.setLastMoistureNotificationTime(now);  // Update last notification time here

        if (prefs.isAutoWateringEnabled()) {
            if (prefs.isIntervalModeEnabled()) return;
            viewModel.publishWithMode("plants/watering", "start", "auto");

            if (prefs.isNotificationsEnabled()) {
                ActivityNotif.makeNotification(
                        requireContext(),
                        "Auto Watering Triggered",
                        "Soil moisture was low. Auto-watering started.",
                        "Watering Notifications"
                );
            }
        } else if (prefs.isNotificationsEnabled()) {
            ActivityNotif.makeNotification(
                    requireContext(),
                    "Water Your Plant",
                    "Soil moisture is low. Please water your plant manually.",
                    "Watering Notifications"
            );
        }
    }





    private void scheduleNextIntervalAlarm() {
        if (!prefs.isIntervalModeEnabled()) return;

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Log.w("ALARM", "Exact alarm permission not granted. Prompting user.");
                return;
            }
        }

        long now = System.currentTimeMillis();
        long nextTriggerTime = prefs.getTriggerTime();
        if (nextTriggerTime <= now) nextTriggerTime = now + 1000;

        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerTime, pendingIntent);
            prefs.setLastTriggerTime(now);
            Log.d("ALARM", "Exact alarm scheduled at: " + nextTriggerTime);
        }
    }

    private void resetSchedulingTime() {
        Calendar calendar = Calendar.getInstance();
        prefs.setCalendarYear(calendar.get(Calendar.YEAR));
        prefs.setCalendarMonth(calendar.get(Calendar.MONTH));
        prefs.setCalendarDay(calendar.get(Calendar.DAY_OF_MONTH));
        int defaultHour = (calendar.get(Calendar.HOUR_OF_DAY) + 1) % 24;
        prefs.setIntervalHour(defaultHour);
        prefs.setIntervalMinute(0);
    }

    private void cancelIntervalAlarm() {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}


