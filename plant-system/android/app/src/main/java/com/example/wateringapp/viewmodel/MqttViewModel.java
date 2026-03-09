package com.example.wateringapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.example.wateringapp.MqttManager.MqttManager;
import com.example.wateringapp.ui.settings.WateringSettingsFragment;
public class MqttViewModel extends ViewModel {

    private final MutableLiveData<Integer> soilMoisture = new MutableLiveData<>();
    private final MutableLiveData<Integer> temperature = new MutableLiveData<>();
    private final MutableLiveData<Integer> humidity = new MutableLiveData<>();
    private final MutableLiveData<Integer> waterReservoir = new MutableLiveData<>();
    private final MutableLiveData<Integer> moistureThreshold = new MutableLiveData<>(0); // Default threshold

    private final MutableLiveData<String> wateringStarted = new MutableLiveData<>();
    private long lastPublishedWateringTime = 0;
    private String lastPublishedMode = null;

    public LiveData<String> getWateringStarted() {
        return wateringStarted;
    }

    public LiveData<Integer> getSoilMoisture() {
        return soilMoisture;
    }

    public LiveData<Integer> getTemperature() {
        return temperature;
    }
    public LiveData<Integer> getHumidity() {
        return humidity;
    }

    public LiveData<Integer> getMoistureThreshold() {
        return moistureThreshold;
    }

    public LiveData<Integer> getWaterReservoirLevel() {
        return waterReservoir;
    }

    public MqttViewModel() {
        MqttManager mqtt = MqttManager.getInstance();


        // Handle incoming messages
        mqtt.setMessageListener((topic, payload) -> {
            if (topic.equals("plants/soil")) {
                try {
                    int moisture = Integer.parseInt(payload);
                    soilMoisture.postValue(moisture);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (topic.equals("plants/temp")) {
                try {
                    int temp = Integer.parseInt(payload);
                    temperature.postValue(temp);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (topic.equals("plants/humidity")) {
                try {
                    int humidityVal = Integer.parseInt(payload);
                    humidity.postValue(humidityVal);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (topic.equals("plants/watering") && payload.equals("start")) {
                long now = System.currentTimeMillis();

                if (now - lastPublishedWateringTime < 3000) {
                    Log.d("MQTT", "Ignored echo watering start for mode: " + lastPublishedMode);
                    return;
                }

                wateringStarted.postValue("scheduled"); // default to "scheduled" when from background
                lastPublishedMode = null;
            } else if (topic.equals("plants/waterLevel")) {
                try {
                    int waterDistance = Integer.parseInt(payload);
                    waterReservoir.postValue(waterDistance);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        mqtt.connect();
        mqtt.restoreSubscriptions();
    }

    // Set moisture threshold
    public void setMoistureThreshold(int threshold) {
        moistureThreshold.postValue(threshold);
    }

    // Publish (default = manual)
    public void publish(String topic, String payload) {
        publishWithMode(topic, payload, "manual");
    }

    // Publish with watering mode
    public void publishWithMode(String topic, String payload, String mode) {
        if (topic.equals("plants/watering") && payload.equals("start")) {
            lastPublishedMode = mode;
            lastPublishedWateringTime = System.currentTimeMillis();

            if (!mode.equals("scheduled")) {
                wateringStarted.postValue(mode); // log manual or auto immediately
            }
        }

        MqttManager.getInstance().publish(topic, payload);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Optional: disconnect if needed
        // MqttManager.getInstance().disconnect();
    }
}
