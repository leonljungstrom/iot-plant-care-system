#include "TempHumiditySensor.h"

TempHumiditySensor::TempHumiditySensor(uint8_t pin) {
    _pin = pin;
    _temperature = 0;
    _humidity = 0;
}

bool TempHumiditySensor::read() {
    uint8_t data[5] = {0};
    uint8_t bitIndex = 0, byteIndex = 0;

    // Send start signal
    pinMode(_pin, OUTPUT);
    digitalWrite(_pin, LOW);
    delay(18);  // DHT11 requires at least 18ms
    digitalWrite(_pin, HIGH);
    delayMicroseconds(40);
    pinMode(_pin, INPUT);

    // Wait for sensor response
    if (digitalRead(_pin) == HIGH) return false;
    delayMicroseconds(80);
    if (digitalRead(_pin) == LOW) return false;
    delayMicroseconds(80);

    // Read 40 bits (5 bytes)
    for (int i = 0; i < 40; i++) {
        // Wait for LOW to HIGH
        unsigned long start = micros();
        while (digitalRead(_pin) == LOW) {
            if (micros() - start > 100) return false;
        }

        // Time HIGH pulse
        start = micros();
        while (digitalRead(_pin) == HIGH) {
            if (micros() - start > 100) return false;
        }

        // If high duration > 40us, it's a '1'
        if ((micros() - start) > 40) {
            data[byteIndex] |= (1 << (7 - bitIndex));
        }

        if (++bitIndex == 8) {
            bitIndex = 0;
            byteIndex++;
        }
    }

    // Verify checksum
    uint8_t checksum = data[0] + data[1] + data[2] + data[3];
    if (data[4] != checksum) return false;

    // Store values (DHT11 only returns integer data)
    _humidity = data[0];
    _temperature = data[2];
    return true;
}

float TempHumiditySensor::readTemperature() {
    return _temperature;
}

float TempHumiditySensor::readHumidity() {
    return _humidity;
}



