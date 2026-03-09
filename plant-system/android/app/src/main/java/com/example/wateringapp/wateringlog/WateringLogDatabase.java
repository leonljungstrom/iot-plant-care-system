package com.example.wateringapp.wateringlog;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WateringLog.class}, version = 1, exportSchema = false)
public abstract class WateringLogDatabase extends RoomDatabase {

    private static volatile WateringLogDatabase INSTANCE;

    public abstract WateringLogDao wateringLogDao();

    public static WateringLogDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (WateringLogDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            WateringLogDatabase.class,
                            "watering_log_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
