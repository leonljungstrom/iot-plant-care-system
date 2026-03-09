package com.example.wateringapp.ui.settings;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class ClockFragment extends DialogFragment {

    public interface OnTimeSelectedListener {
        void onTimeSelected(int hourOfDay, int minute);
    }

    private OnTimeSelectedListener listener;
    private int initialHour = 12;
    private int initialMinute = 0;

    public void setInitialTime(int hour, int minute) {
        this.initialHour = hour;
        this.initialMinute = minute;
    }

    public void setOnTimeSelectedListener(OnTimeSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new TimePickerDialog(
                getActivity(),
                (view, hourOfDay, minute) -> {
                    if (listener != null) {
                        listener.onTimeSelected(hourOfDay, minute);
                    }
                },
                initialHour,
                initialMinute,
                true // 24-hour format
        );
    }
}

