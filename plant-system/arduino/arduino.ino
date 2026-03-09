/*
 * Plant Care System
 * Embedded controller for monitoring soil moisture, temperature,
 * humidity, and water level, with MQTT-based remote control
 * and automatic watering support.
 */

#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include "Certs.h"
#include "Screen.h"
#include "SoilSensor.h"
#include "WaterPump.h"
#include "TempHumiditySensor.h"
#include "UltraSonicSensor.h"
#include "Secrets.h"

// Pin configuration
const int SOIL_SENSOR_PIN = A2;
const int PUMP_PIN = A3;
const int TEMP_HUMIDITY_PIN = D5;
const int ULTRASONIC_SENSOR_PIN = A7;

// MQTT topics
const char* TOPIC_WATERING = "plants/watering";
const char* TOPIC_WATER_THRESHOLD = "plants/waterPercentage";
const char* TOPIC_WATER_AMOUNT = "plants/wateringAmount";
const char* TOPIC_TEMP = "plants/temp";
const char* TOPIC_HUMIDITY = "plants/humidity";
const char* TOPIC_SOIL = "plants/soil";
const char* TOPIC_WATER_LEVEL = "plants/waterLevel";

// MQTT configuration
const int MQTT_PORT = 8883;
const char* CLIENT_ID = "plant-care-device-01";
unsigned long lastMqttReconnectAttempt = 0;
const unsigned long MQTT_RECONNECT_INTERVAL_MS = 5000;

// Default values
const int DEFAULT_WATERING_THRESHOLD = 20;   // percent
const int DEFAULT_WATERING_AMOUNT_ML = 50;   // ml
const float PUMP_FLOW_RATE_ML_PER_SECOND = 50.0f;

// Publish intervals
const unsigned long SOIL_PUBLISH_INTERVAL_MS = 5000;
const unsigned long TEMP_HUMIDITY_PUBLISH_INTERVAL_MS = 10000;

// Allowed ranges
const int MIN_WATERING_THRESHOLD = 0;
const int MAX_WATERING_THRESHOLD = 100;
const int MIN_WATERING_AMOUNT_ML = 1;
const int MAX_WATERING_AMOUNT_ML = 1000;

// Instances
WiFiClientSecure secureClient;
PubSubClient client(secureClient);
SoilSensor soilSensor(SOIL_SENSOR_PIN);
Screen screen;
WaterPump pump(PUMP_PIN);
TempHumiditySensor tempHumiditySensor(TEMP_HUMIDITY_PIN);
UltraSonicSensor ultrasonicSensor(ULTRASONIC_SENSOR_PIN);

// Runtime settings
int wateringThreshold = DEFAULT_WATERING_THRESHOLD;
int wateringAmountMl = DEFAULT_WATERING_AMOUNT_ML;

// Loop tracking
unsigned long lastSoilPublish = 0;
unsigned long lastTempHumidityPublish = 0;
unsigned long lastWifiReconnectAttempt = 0;
const unsigned long WIFI_RECONNECT_INTERVAL_MS = 10000;
bool wifiWasConnected = false;

void startWifiConnection() {
    Serial.print("Starting WiFi connection...");
    WiFi.begin(wifiSsid, wifiPassword);
}

void handleWifiConnection(unsigned long currentMillis) {
    auto wifiStatus = WiFi.status();

    if (wifiStatus == WL_CONNECTED) {
        if (!wifiWasConnected) {
            Serial.println(" WiFi connected!");
            wifiWasConnected = true;
        }
        return;
    }

    if (wifiWasConnected) {
        Serial.println("WiFi disconnected.");
        wifiWasConnected = false;
    }

    if (currentMillis - lastWifiReconnectAttempt >= WIFI_RECONNECT_INTERVAL_MS) {
        lastWifiReconnectAttempt = currentMillis;
        Serial.println("Retrying WiFi connection...");
        WiFi.disconnect();
        WiFi.begin(wifiSsid, wifiPassword);
    }
}

void subscribeToTopics() {
    client.subscribe(TOPIC_WATERING);
    client.subscribe(TOPIC_WATER_THRESHOLD);
    client.subscribe(TOPIC_WATER_AMOUNT);
    client.subscribe(TOPIC_TEMP);
    client.subscribe(TOPIC_HUMIDITY);
}

bool reconnectMqtt() {
    Serial.print("Connecting to MQTT...");

    if (client.connect(CLIENT_ID, mqttUser, mqttPassword)) {
        subscribeToTopics();
        Serial.println(" connected!");
        return true;
    } else {
        Serial.print(" failed, rc=");
        Serial.println(client.state());
        return false;
    }
}

void handleWaterThresholdMessage(const String& message) {
    int newThreshold = message.toInt();

    if (newThreshold >= MIN_WATERING_THRESHOLD && newThreshold <= MAX_WATERING_THRESHOLD) {
        wateringThreshold = newThreshold;
        screen.displayThreshold(wateringThreshold);

        Serial.print("Updated watering threshold: ");
        Serial.println(wateringThreshold);
    } else {
        Serial.print("Ignored invalid watering threshold: ");
        Serial.println(newThreshold);
    }
}

void handleWateringMessage(const String& message) {
    if (message == "start" && !pump.isRunning()) {
        unsigned long durationMs =
            (unsigned long)((wateringAmountMl / PUMP_FLOW_RATE_ML_PER_SECOND) * 1000.0f);

        Serial.print("Watering ");
        Serial.print(wateringAmountMl);
        Serial.println(" ml");

        pump.runForMilliseconds(durationMs);
    } else if (message == "stop" && pump.isRunning()) {
        pump.turnOff();
        Serial.println("Watering stopped");
    }
}

void handleWateringAmountMessage(const String& message) {
    int newAmount = message.toInt();

    if (newAmount >= MIN_WATERING_AMOUNT_ML && newAmount <= MAX_WATERING_AMOUNT_ML) {
        wateringAmountMl = newAmount;

        Serial.print("Updated watering amount to ");
        Serial.print(wateringAmountMl);
        Serial.println(" ml");
    } else {
        Serial.print("Ignored invalid watering amount: ");
        Serial.println(newAmount);
    }
}

// Callback to handle MQTT subscriptions
void callback(char* topic, byte* payload, unsigned int length) {
    String message;
    for (unsigned int i = 0; i < length; i++) {
        message += (char)payload[i];
    }

    if (strcmp(topic, TOPIC_WATER_THRESHOLD) == 0) {
        handleWaterThresholdMessage(message);
    } else if (strcmp(topic, TOPIC_WATERING) == 0) {
        handleWateringMessage(message);
    } else if (strcmp(topic, TOPIC_WATER_AMOUNT) == 0) {
        handleWateringAmountMessage(message);
    }
}

void publishSoilAndWaterLevel() {
    int raw = soilSensor.readRaw();
    int percent = soilSensor.readPercent();
    int distance = ultrasonicSensor.getDistance();

    Serial.print("Raw: ");
    Serial.print(raw);
    Serial.print(" | Moisture: ");
    Serial.print(percent);
    Serial.println("%");

    Serial.print("Watering threshold: ");
    Serial.println(wateringThreshold);

    screen.displayMoisture(percent);

    if (client.connected()) {
        char soilMessage[10];
        snprintf(soilMessage, sizeof(soilMessage), "%d", percent);
        client.publish(TOPIC_SOIL, soilMessage);

        char distanceMessage[10];
        snprintf(distanceMessage, sizeof(distanceMessage), "%d", distance);
        client.publish(TOPIC_WATER_LEVEL, distanceMessage);
    }
}

void publishTemperatureAndHumidity() {
    if (tempHumiditySensor.read()) {
        int temperature = tempHumiditySensor.readTemperature();
        int humidity = tempHumiditySensor.readHumidity();

        Serial.print("Temp: ");
        Serial.print(temperature);
        Serial.print(" °C | Humidity: ");
        Serial.print(humidity);
        Serial.println(" %");

        if (client.connected()) {
            char temperatureMessage[10];
            char humidityMessage[10];

            snprintf(temperatureMessage, sizeof(temperatureMessage), "%d", temperature);
            snprintf(humidityMessage, sizeof(humidityMessage), "%d", humidity);

            client.publish(TOPIC_TEMP, temperatureMessage);
            client.publish(TOPIC_HUMIDITY, humidityMessage);
        }
    } else {
        Serial.println("Failed to read from TempHumiditySensor");
    }
}

void setup() {
    Serial.begin(115200);
    delay(1500);

    secureClient.setCACert(hiveMQ_cert);

    client.setServer(mqttServer, MQTT_PORT);
    client.setCallback(callback);

    pump.begin();
    screen.begin();
    screen.displayThreshold(wateringThreshold);

    startWifiConnection();
}

void loop() {
    unsigned long currentMillis = millis();

    handleWifiConnection(currentMillis);

    if (WiFi.status() == WL_CONNECTED) {
        if (!client.connected()) {
            if (currentMillis - lastMqttReconnectAttempt >= MQTT_RECONNECT_INTERVAL_MS) {
                lastMqttReconnectAttempt = currentMillis;
                reconnectMqtt();
            }
        } else {
            client.loop();
        }
    }

    pump.update();

    if (currentMillis - lastSoilPublish >= SOIL_PUBLISH_INTERVAL_MS) {
        lastSoilPublish = currentMillis;
        publishSoilAndWaterLevel();
    }

    if (currentMillis - lastTempHumidityPublish >= TEMP_HUMIDITY_PUBLISH_INTERVAL_MS) {
        lastTempHumidityPublish = currentMillis;
        publishTemperatureAndHumidity();
    }
}