package com.example.wateringapp.ui.settings;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class CalendarFragment extends DialogFragment {

    public interface OnDateSelectedListener {
        void onDateSelected(String formattedDate);
    }

    private OnDateSelectedListener listener;
    private int initialYear, initialMonth, initialDay;

    public void setInitialDate(int year, int month, int day) {
        this.initialYear = year;
        this.initialMonth = month;
        this.initialDay = day;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), (view, year, month, day) -> {
            String date = year + "-" + (month + 1) + "-" + day;
            if (listener != null) {
                listener.onDateSelected(date);
            }
        }, initialYear, initialMonth, initialDay);

        // Prevent selecting past dates. -1000 to avoid precision errors
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        return dialog;
    }
}

