IoT Plant Care System

An IoT-based system that monitors plant conditions and automatically
waters plants using sensor data and an Android control app.

------------------------------------------------------------------------

About The Project

Demo Video
https://www.youtube.com/watch?v=MPgn9T80wQg

Plants liven up any space, but keeping them healthy can sometimes be
difficult.
This project simplifies plant care by combining embedded systems, IoT
communication, and a mobile application.

The system continuously monitors:

-   Soil moisture
-   Water reservoir level
-   Temperature
-   Humidity

Using this data, the system can automatically water plants, notify the
user when conditions require attention, and allow manual control through
a mobile application.

Main Components

-   Embedded controller (Arduino) – reads sensors and controls the pump
-   Android application – displays plant data and allows user
    interaction
-   MQTT broker – communication layer between devices

------------------------------------------------------------------------

Software Architecture

The system uses a two-tier architecture.

Sensor Tier

Runs on a microcontroller and is responsible for:

-   Reading sensor values
-   Controlling the water pump
-   Publishing sensor data via MQTT
-   Receiving commands from the mobile app

User Tier

The Android application allows users to:

-   Monitor plant status
-   Adjust watering settings
-   Trigger manual watering
-   View watering history

Both tiers communicate through an MQTT broker.

------------------------------------------------------------------------

Project Structure

    plant-system
    │
    ├── android
    │   └── Android application
    │
    ├── arduino
    │   └── Embedded controller code
    │
    └── images
        └── Architecture diagrams

------------------------------------------------------------------------

Getting Started

Clone the repository

    git clone https://github.com/leonljungstrom/iot-plant-care-system.git
    cd iot-plant-care-system

------------------------------------------------------------------------

Prerequisites

Software

-   Arduino IDE
    https://www.arduino.cc/en/software

-   Android Studio
    https://developer.android.com/studio

Hardware

-   Microcontroller (e.g. Wio Terminal)
-   Water pump + tubing
-   Relay module
-   Soil moisture sensor
-   DHT11 temperature/humidity sensor
-   Ultrasonic sensor
-   Breadboard
-   Jumper wires
-   Power supply

------------------------------------------------------------------------

Configuration

Create the file:

    plant-system/arduino/Secrets.h

Example:

    const char* wifiSsid = "YOUR_WIFI_NAME";
    const char* wifiPassword = "YOUR_WIFI_PASSWORD";

    const char* mqttServer = "YOUR_MQTT_SERVER";
    const char* mqttUser = "YOUR_MQTT_USER";
    const char* mqttPassword = "YOUR_MQTT_PASSWORD";

This file is ignored by Git to avoid exposing credentials.

------------------------------------------------------------------------

Hardware Setup

Soil Moisture Sensor

-   VCC → 5V
-   GND → GND
-   Signal → A2

Insert the sensor into the plant soil.

------------------------------------------------------------------------

Relay & Water Pump

-   Relay control pin → A3
-   Relay switches the power supply to the pump

Place the pump in a water reservoir and connect tubing to the plant.

------------------------------------------------------------------------

Ultrasonic Sensor

-   VCC → 5V
-   GND → GND
-   Signal → A7

Mount the ultrasonic sensor above the reservoir to measure water level.

------------------------------------------------------------------------

Temperature & Humidity Sensor (DHT11)

-   VCC → 5V
-   GND → GND
-   Data → D5

------------------------------------------------------------------------

Usage

Once powered on the system automatically monitors the plant environment.

Features

-   Soil moisture monitoring
-   Automatic watering
-   Manual watering via Android app
-   Water reservoir monitoring
-   Temperature and humidity monitoring
-   Care suggestion presets
-   Watering history logs

------------------------------------------------------------------------
