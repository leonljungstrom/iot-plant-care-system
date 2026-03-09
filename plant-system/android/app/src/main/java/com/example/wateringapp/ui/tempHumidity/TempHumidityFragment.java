package com.example.wateringapp.ui.tempHumidity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.wateringapp.Notifictations.ActivityNotif;
import com.example.wateringapp.R;
import com.example.wateringapp.settingspreferences.SettingsPreferences;
import com.example.wateringapp.viewmodel.MqttViewModel;

public class TempHumidityFragment extends Fragment {
    private boolean isTempHumidityAlertsEnabled = false;


    private MqttViewModel viewModel;
    private SettingsPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_temp_humidity, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MqttViewModel.class);
        prefs = new SettingsPreferences(requireContext());

        //references
        Switch tempAlertSwitch = view.findViewById(R.id.switch_temp);
        TextView tempStatus = view.findViewById(R.id.temperatureText);
        TextView humidityStatus = view.findViewById(R.id.humidityText);
        TextView tempRange = view.findViewById(R.id.currentTempRangeText);
        TextView humidityRange = view.findViewById(R.id.currentHumidityRangeText);
        Button saveButton = view.findViewById(R.id.btn_save_settings);
        EditText minTempInput = view.findViewById(R.id.minTempInput);
        EditText maxTempInput = view.findViewById(R.id.maxTempInput);
        EditText minHumidityInput = view.findViewById(R.id.minHumidityInput);
        EditText maxHumidityInput = view.findViewById(R.id.maxHumidityInput);



        //read previous settings and set values
        int minTemp = prefs.getMinTemp();
        int maxTemp = prefs.getMaxTemp();
        int minHumidity = prefs.getMinHumidity();
        int maxHumidity = prefs.getMaxHumidity();
        boolean tempAlerts = prefs.isTempHumidityAlertsEnabled();

        tempAlertSwitch.setChecked(tempAlerts);

        tempRange.setText("Temperature: "+ minTemp +"°C - " + maxTemp + "°C");
        humidityRange.setText("Humidity: "+ minHumidity +"% - " + maxHumidity + "%");


        // Button: Save Temp/Humidity Settings
        saveButton.setOnClickListener(v -> {
            if (minTempInput.getText().toString().trim().isEmpty() ||
                maxTempInput.getText().toString().trim().isEmpty() ||
                minHumidityInput.getText().toString().trim().isEmpty() ||
                maxHumidityInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                int minTempVal = Integer.parseInt(minTempInput.getText().toString());
                int maxTempVal = Integer.parseInt(maxTempInput.getText().toString());
                int minHumidityVal = Integer.parseInt(minHumidityInput.getText().toString());
                int maxHumidityVal = Integer.parseInt(maxHumidityInput.getText().toString());

                prefs.setMinTemp(minTempVal);
                prefs.setMaxTemp(maxTempVal);
                prefs.setMinHumidity(minHumidityVal);
                prefs.setMaxHumidity(maxHumidityVal);

                tempRange.setText("Temperature: "+ prefs.getMinTemp() +"°C - " + prefs.getMaxTemp() + "°C");
                humidityRange.setText("Humidity: "+ prefs.getMinHumidity() +"% - " + prefs.getMaxHumidity() + "%");

                Toast.makeText(requireContext(), "Settings saved!", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
            }
        });


        // Toggle: Temp Alerts
        tempAlertSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.setTempHumidityAlertsEnabled(isChecked)
        );

        //Using viewmodel to get values
        viewModel.getTemperature().observe(getViewLifecycleOwner(), temperature -> {
            tempStatus.setText(temperature + "°C");

            if (temperature < prefs.getMinTemp() || temperature > prefs.getMaxTemp()) {
                if (prefs.isTempHumidityAlertsEnabled()) {
                        ActivityNotif.makeNotification(requireContext(),
                                "Temperature out of range",
                                "Temperature out of range, check on your plant!",
                                "Temperature Alert");
                    }
            }
        });

        viewModel.getHumidity().observe(getViewLifecycleOwner(), humidity -> {
            humidityStatus.setText(humidity + "%");

            if (humidity < prefs.getMinHumidity() || humidity > prefs.getMaxHumidity()) {
                if (prefs.isTempHumidityAlertsEnabled()) {
                    ActivityNotif.makeNotification(requireContext(),
                            "Humidity out of range",
                            "Humidity out of range, check on your plant!",
                            "Humidity Alert");
                }
            }
        });

        return view;
    }
}



