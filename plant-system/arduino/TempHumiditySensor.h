#ifndef TEMP_HUMIDITY_SENSOR_H
#define TEMP_HUMIDITY_SENSOR_H

#include <Arduino.h>

class TempHumiditySensor {
private:
    uint8_t _pin;
    float _temperature;
    float _humidity;

public:
    TempHumiditySensor(uint8_t pin);
    bool read();
    float readTemperature();
    float readHumidity();
};

#endif