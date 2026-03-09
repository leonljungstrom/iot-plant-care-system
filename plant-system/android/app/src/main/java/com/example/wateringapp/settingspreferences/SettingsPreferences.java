package com.example.wateringapp.settingspreferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class SettingsPreferences {
    private static final String PREF_NAME = "plant_settings";
    public static boolean hasNotifiedOnce = false;





    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_AUTO_WATER = "auto_watering_enabled";
    private static final String KEY_THRESHOLD = "moisture_threshold";
    private static final String KEY_WATER_AMOUNT = "water_amount";
    private static final String KEY_RESERVOIR_HEIGHT = "reservoir_height";

    private static final String KEY_TEMP_HUMIDITY_ALERTS = "temp_humidity_alerts";

    private static final String KEY_MIN_TEMP = "min_temp";

    private static final String KEY_MAX_TEMP = "max_temp";

    private static final String KEY_MIN_HUMIDITY = "min_humidity";

    private static final String KEY_MAX_HUMIDITY = "max_humidity";

    private static final String KEY_LAST_TRIGGER = "last_trigger";

    private static final String KEY_LAST_TRIGGER_TIME = "last_trigger_time";

    private static final String KEY_INTERVAL_MODE = "interval_mode";
    private static final String KEY_INTERVAL_DAYS = "interval_days";

    private static final String KEY_INTERVAL_HOUR = "interval_hour";
    private static final String KEY_INTERVAL_MINUTE = "interval_minute";

    private static final String KEY_CALENDAR_YEAR = "calendar_year";
    private static final String KEY_CALENDAR_MONTH = "calendar_month";
    private static final String KEY_CALENDAR_DAY = "calendar_day";

    private final SharedPreferences prefs;

    // Session notification flag
    private static boolean notificationShownThisSession = false;



    public long getLastReservoirNotificationTime() {
        return prefs.getLong("lastReservoirNotification", 0);
    }

    public void setLastReservoirNotificationTime(long time) {
        prefs.edit().putLong("lastReservoirNotification", time).apply();
    }
    public long getLastMoistureNotificationTime() {
        return prefs.getLong("lastMoistureNotification", 0);
    }

    public void setLastMoistureNotificationTime(long time) {
        prefs.edit().putLong("lastMoistureNotification", time).apply();
    }



    public SettingsPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public long getIntervalMillis() {
        return this.getIntervalDays() * 24 * 60 * 60 * 1000L;
    }

    public long getTriggerTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,  getCalendarYear());
        cal.set(Calendar.MONTH, getCalendarMonth());
        cal.set(Calendar.DAY_OF_MONTH, getCalendarDay());
        cal.set(Calendar.HOUR_OF_DAY, getIntervalHour());
        cal.set(Calendar.MINUTE, getIntervalMinute());
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    // Notifications
    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }
    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, false);
    }

    // AutoWatering
    public void setAutoWateringEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_WATER, enabled).apply();
    }
    public boolean isAutoWateringEnabled() {
        return prefs.getBoolean(KEY_AUTO_WATER, false);
    }

    // Interval
    public void setIntervalModeEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_INTERVAL_MODE, enabled).apply();
    }
    public boolean isIntervalModeEnabled() {
        return prefs.getBoolean(KEY_INTERVAL_MODE, false);
    }

    // Interval Days
    public void setIntervalDays(int value) {
        prefs.edit().putInt(KEY_INTERVAL_DAYS, value).apply();
    }
    public int getIntervalDays() {
        return prefs.getInt(KEY_INTERVAL_DAYS, 5);
    }

    // Interval Clock
    public void setIntervalHour(int hour) {
        prefs.edit().putInt(KEY_INTERVAL_HOUR, hour).apply();
    }
    public int getIntervalHour() {
        int defaultHour = (today.get(Calendar.HOUR_OF_DAY) + 1) % 24;
        return prefs.getInt(KEY_INTERVAL_HOUR, defaultHour);
    };
    public void setIntervalMinute(int minute) {
        prefs.edit().putInt(KEY_INTERVAL_MINUTE, minute).apply();
    }
    public int getIntervalMinute() {
        return prefs.getInt(KEY_INTERVAL_MINUTE, 0);
    };

    // Interval Calendar
    Calendar today = Calendar.getInstance();
    public void setCalendarYear(int year) {
        prefs.edit().putInt(KEY_CALENDAR_YEAR, year).apply();
    }
    public int getCalendarYear() {
        return prefs.getInt(KEY_CALENDAR_YEAR, today.get(Calendar.YEAR));
    }

    public void setCalendarMonth(int zeroBasedMonth) {
        prefs.edit().putInt(KEY_CALENDAR_MONTH, zeroBasedMonth).apply();
    }
    public int getCalendarMonth() {
        return prefs.getInt(KEY_CALENDAR_MONTH, today.get(Calendar.MONTH));
    }

    public void setCalendarDay(int day) {
        prefs.edit().putInt(KEY_CALENDAR_DAY, day).apply();
    }
    public int getCalendarDay() {
        return prefs.getInt(KEY_CALENDAR_DAY, today.get(Calendar.DAY_OF_MONTH));
    }

    // Alarm
    public void setLastTriggerTime(long millis) {
        prefs.edit().putLong(KEY_LAST_TRIGGER_TIME, millis).apply();
    }

    public long getLastTriggerTime() {
        return prefs.getLong(KEY_LAST_TRIGGER_TIME, 0); // 0 by default
    }

    // Moisture Threshold
    public void setMoistureThreshold(int value) {
        prefs.edit().putInt(KEY_THRESHOLD, value).apply();
    }
    public int getMoistureThreshold() {
        return prefs.getInt(KEY_THRESHOLD, 0);
    }

    // Water Amount
    public void setWaterAmount(int value) {
        prefs.edit().putInt(KEY_WATER_AMOUNT, value).apply();
    }
    public int getWaterAmount() {
        return prefs.getInt(KEY_WATER_AMOUNT, 30);
    }

    // Temp Alerts
    public void setTempHumidityAlertsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_TEMP_HUMIDITY_ALERTS, enabled).apply();
    }

    public boolean isTempHumidityAlertsEnabled() {
        return prefs.getBoolean(KEY_TEMP_HUMIDITY_ALERTS, false);
    }

    public void setMinTemp(int value) {prefs.edit().putInt(KEY_MIN_TEMP, value).apply();}

    public int getMinTemp() {return prefs.getInt(KEY_MIN_TEMP, 0);}

    public void setMaxTemp(int value) {prefs.edit().putInt(KEY_MAX_TEMP, value).apply();}

    public int getMaxTemp() {return prefs.getInt(KEY_MAX_TEMP, 50);}

    public void setMinHumidity(int value) {prefs.edit().putInt(KEY_MIN_HUMIDITY, value).apply();}

    public int getMinHumidity() {return prefs.getInt(KEY_MIN_HUMIDITY, 0);}

    public void setMaxHumidity(int value) {prefs.edit().putInt(KEY_MAX_HUMIDITY, value).apply();}

    public int getMaxHumidity() {return prefs.getInt(KEY_MAX_HUMIDITY, 100);}

    // Reservoir Height
    public void setReservoirHeight(int value) {prefs.edit().putInt(KEY_RESERVOIR_HEIGHT, value).apply();}

    public int getReservoirHeight() {
        return prefs.getInt(KEY_RESERVOIR_HEIGHT, 30);
    }

    // Notification session flag logic
    public static boolean hasShownNotificationThisSession() {
        return notificationShownThisSession;
    }

    public static void markNotificationAsShownThisSession() {
        notificationShownThisSession = true;
    }

    public static void resetNotificationSessionFlag() {
        notificationShownThisSession = false;
    }
}