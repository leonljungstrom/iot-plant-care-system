package com.example.wateringapp.wateringlog;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WateringLogDao {

    @Insert
    void insert(WateringLog log); //saves watering log to database

    @Query("SELECT * FROM WateringLog ORDER BY timestamp DESC LIMIT 10")
    LiveData<List<WateringLog>> getLast10Logs(); //returns 10 latest logs
}
