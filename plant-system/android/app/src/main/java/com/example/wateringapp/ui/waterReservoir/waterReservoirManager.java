package com.example.wateringapp.ui.waterReservoir;

import android.content.Context;
import com.example.wateringapp.Notifictations.ActivityNotif;

public class waterReservoirManager {

    public static int calculateWaterPercentage(int reservoirHeight, int waterLevelDistance){
        if (reservoirHeight <= 1) {
            // Can't calculate a percentage with invalid or too-small reservoir height
            return 0;
        }

        int usableHeight = reservoirHeight - 1;
        int filledHeight = usableHeight - waterLevelDistance;
        filledHeight = Math.max(0, filledHeight); // clamp negative values

        return (filledHeight * 100) / usableHeight;
    }
    public static void reservoirNotify(int waterPercentage, Context context){
        ActivityNotif.makeNotification(context,
                "Water reservoir level is at " + waterPercentage + "%.",
                "The water level in your reservoir, and needs to be refilled.",
                "Reservoir Notifications");
    }
}
