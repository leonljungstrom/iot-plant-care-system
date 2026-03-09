package com.example.wateringapp.wateringlog;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WateringLog {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long timestamp;
    public int amount;
    public String mode;    // "manual" or "auto"
}
