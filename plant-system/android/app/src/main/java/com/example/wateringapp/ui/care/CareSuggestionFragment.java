package com.example.wateringapp.ui.care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Switch;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.wateringapp.R;
import com.example.wateringapp.settingspreferences.SettingsPreferences;
import com.example.wateringapp.viewmodel.MqttViewModel;

public class CareSuggestionFragment extends Fragment {

    private TextView textViewCareSuggestions;
    private Switch switchDesert, switchTropical, switchTemperate, switchMountain, switchGrassland, switchMediterranean, switchIndoor;
    private MqttViewModel viewModel;
    private SettingsPreferences settingsPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_care_suggestion, container, false);

        textViewCareSuggestions = view.findViewById(R.id.textViewCareSuggestions);
        settingsPrefs = new SettingsPreferences(requireContext());
        viewModel = new ViewModelProvider(requireActivity()).get(MqttViewModel.class);

        switchDesert = view.findViewById(R.id.switchDesert);
        switchTropical = view.findViewById(R.id.switchTropical);
        switchTemperate = view.findViewById(R.id.switchTemperate);
        switchMountain = view.findViewById(R.id.switchMountain);
        switchGrassland = view.findViewById(R.id.switchGrassland);
        switchMediterranean = view.findViewById(R.id.switchMediterranean);
        switchIndoor = view.findViewById(R.id.switchIndoor);

        restoreSwitchState();

        view.findViewById(R.id.buttonDesert).setOnClickListener(v -> showCareSuggestions("Desert"));
        view.findViewById(R.id.buttonTropical).setOnClickListener(v -> showCareSuggestions("Tropical"));
        view.findViewById(R.id.buttonTemperate).setOnClickListener(v -> showCareSuggestions("Temperate Forest"));
        view.findViewById(R.id.buttonMountain).setOnClickListener(v -> showCareSuggestions("Mountain/Alpine"));
        view.findViewById(R.id.buttonGrassland).setOnClickListener(v -> showCareSuggestions("Grassland/Savanna"));
        view.findViewById(R.id.buttonMediterranean).setOnClickListener(v -> showCareSuggestions("Mediterranean"));
        view.findViewById(R.id.buttonIndoor).setOnClickListener(v -> showCareSuggestions("Indoor"));

        setupSwitchListeners();

        return view;
    }

    private void setupSwitchListeners() {
        setupSwitch(switchDesert, "Desert", "switchDesert");
        setupSwitch(switchTropical, "Tropical", "switchTropical");
        setupSwitch(switchTemperate, "Temperate Forest", "switchTemperate");
        setupSwitch(switchMountain, "Mountain/Alpine", "switchMountain");
        setupSwitch(switchGrassland, "Grassland/Savanna", "switchGrassland");
        setupSwitch(switchMediterranean, "Mediterranean", "switchMediterranean");
        setupSwitch(switchIndoor, "Indoor", "switchIndoor");
    }

    private void setupSwitch(Switch sw, String category, String key) {
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // If the switch is turned on, turn off all other switches
            if (isChecked) {
                turnOffOtherSwitches(category);
            }
            updateWateringSettings(category, isChecked);
            requireContext().getSharedPreferences("switch_states", 0)
                    .edit().putBoolean(key, isChecked).apply();
        });
    }

    private void turnOffOtherSwitches(String selectedCategory) {
        // Turn off all switches except the one that was selected
        if (!selectedCategory.equals("Desert")) switchDesert.setChecked(false);
        if (!selectedCategory.equals("Tropical")) switchTropical.setChecked(false);
        if (!selectedCategory.equals("Temperate Forest")) switchTemperate.setChecked(false);
        if (!selectedCategory.equals("Mountain/Alpine")) switchMountain.setChecked(false);
        if (!selectedCategory.equals("Grassland/Savanna")) switchGrassland.setChecked(false);
        if (!selectedCategory.equals("Mediterranean")) switchMediterranean.setChecked(false);
        if (!selectedCategory.equals("Indoor")) switchIndoor.setChecked(false);
    }

    private void restoreSwitchState() {
        var prefs = requireContext().getSharedPreferences("switch_states", 0);
        switchDesert.setChecked(prefs.getBoolean("switchDesert", false));
        switchTropical.setChecked(prefs.getBoolean("switchTropical", false));
        switchTemperate.setChecked(prefs.getBoolean("switchTemperate", false));
        switchMountain.setChecked(prefs.getBoolean("switchMountain", false));
        switchGrassland.setChecked(prefs.getBoolean("switchGrassland", false));
        switchMediterranean.setChecked(prefs.getBoolean("switchMediterranean", false));
        switchIndoor.setChecked(prefs.getBoolean("switchIndoor", false));
    }

    private void showCareSuggestions(String category) {
        String careSuggestions = "", wateringSchedule = "", wateringAmount = "", moistureThreshold = "";

        switch (category) {
            case "Desert":
                careSuggestions = "Water sparingly";
                wateringSchedule = "Water 2 days per week.";
                wateringAmount = "Water 10-15 ml per day.";
                moistureThreshold = "Moisture Threshold: 20%";
                break;
            case "Tropical":
                careSuggestions = "High humidity";
                wateringSchedule = "Water 4 days per week.";
                wateringAmount = "Water 25-50 ml per day.";
                moistureThreshold = "Moisture Threshold: 70%";
                break;
            case "Temperate Forest":
                careSuggestions = "Moderate watering";
                wateringSchedule = "Water 3 days per week.";
                wateringAmount = "Water 50-75 ml per day.";
                moistureThreshold = "Moisture Threshold: 50%";
                break;
            case "Mountain/Alpine":
                careSuggestions = "Minimal watering";
                wateringSchedule = "Water 1-2 days per week.";
                wateringAmount = "Water 20-30 ml per day.";
                moistureThreshold = "Moisture Threshold: 30%";
                break;
            case "Grassland/Savanna":
                careSuggestions = "Moderate watering";
                wateringSchedule = "Water 3 days per week.";
                wateringAmount = "Water 50-100 ml per day.";
                moistureThreshold = "Moisture Threshold: 40%";
                break;
            case "Mediterranean":
                careSuggestions = "Deep watering, less often";
                wateringSchedule = "Water 2-3 days per week.";
                wateringAmount = "Water 30-50 ml per day.";
                moistureThreshold = "Moisture Threshold: 25%";
                break;
            case "Indoor":
                careSuggestions = "Avoid overwatering";
                wateringSchedule = "Water 4 days per week.";
                wateringAmount = "Water 25-40 ml per day.";
                moistureThreshold = "Moisture Threshold: 60%";
                break;
        }

        textViewCareSuggestions.setText(String.format(
                "%s\n\nWatering Schedule: %s\nWatering Amount: %s\nMoisture Threshold: %s",
                careSuggestions, wateringSchedule, wateringAmount, moistureThreshold
        ));
    }

    private void updateWateringSettings(String category, boolean isChecked) {
        int waterAmount = 0;
        int moistureThreshold = 0;

        if (isChecked) {
            switch (category) {
                case "Desert": waterAmount = 15; moistureThreshold = 20; break;
                case "Tropical": waterAmount = 50; moistureThreshold = 70; break;
                case "Temperate Forest": waterAmount = 75; moistureThreshold = 50; break;
                case "Mountain/Alpine": waterAmount = 30; moistureThreshold = 30; break;
                case "Grassland/Savanna": waterAmount = 100; moistureThreshold = 40; break;
                case "Mediterranean": waterAmount = 50; moistureThreshold = 25; break;
                case "Indoor": waterAmount = 40; moistureThreshold = 60; break;
            }

            settingsPrefs.setWaterAmount(waterAmount);
            settingsPrefs.setMoistureThreshold(moistureThreshold);
            viewModel.publish("plants/wateringAmount", String.valueOf(waterAmount));
            viewModel.publish("plants/waterPercentage", String.valueOf(moistureThreshold));
        }
    }
}
