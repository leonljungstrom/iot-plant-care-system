package com.example.wateringapp.ui.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.wateringapp.R;

public class NumberPickerDialogFragment extends DialogFragment {

    private OnNumberSelectedListener listener;
    private int initialValue = 5;

    public interface OnNumberSelectedListener {
        void onNumberSelected(int number);
    }

    public void setOnNumberSelectedListener(OnNumberSelectedListener listener) {
        this.listener = listener;
    }

    public void setInitialValue(int initialValue) {
        this.initialValue = initialValue;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.fragment_number_picker);
        dialog.setTitle("Select Interval Days");

        NumberPicker numberPicker = dialog.findViewById(R.id.number_picker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(30);
        numberPicker.setValue(initialValue);

        dialog.findViewById(R.id.btn_ok).setOnClickListener(v -> {
            if (listener != null) {
                listener.onNumberSelected(numberPicker.getValue());
            }
            dismiss();
        });

        dialog.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());

        return dialog;
    }
}

