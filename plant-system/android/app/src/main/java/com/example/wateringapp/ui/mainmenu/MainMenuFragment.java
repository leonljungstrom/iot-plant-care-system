package com.example.wateringapp.ui.mainmenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wateringapp.R;
import com.example.wateringapp.ui.care.CareSuggestionFragment;
import com.example.wateringapp.ui.settings.WateringSettingsFragment;
import com.example.wateringapp.ui.tempHumidity.TempHumidityFragment;

public class MainMenuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the new XML layout (make sure the filename matches R.layout)
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        // Set up click listeners for each ImageButton
        view.findViewById(R.id.btn_watering_settings).setOnClickListener(v ->
                navigateTo(new WateringSettingsFragment()));

        view.findViewById(R.id.btn_temperature_humidity).setOnClickListener(v ->
                navigateTo(new TempHumidityFragment()));

        view.findViewById(R.id.btn_care_suggestions).setOnClickListener(v ->
                navigateTo(new CareSuggestionFragment()));

        return view;
    }

    private void navigateTo(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit();
    }
}
