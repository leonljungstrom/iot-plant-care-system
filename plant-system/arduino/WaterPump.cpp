#include "WaterPump.h"
#include <Arduino.h>

WaterPump::WaterPump(int pin) {
    this->pin = pin;
    isOn = false;
}

void WaterPump::begin() {
    pinMode(pin, OUTPUT);
    digitalWrite(pin, LOW);
}

void WaterPump::turnOn() {
    digitalWrite(pin, HIGH);
    isOn = true;
}

void WaterPump::turnOff() {
    digitalWrite(pin, LOW);
    isOn = false;
}

bool WaterPump::isRunning() {
    return isOn;
}
void WaterPump::runForMilliseconds(unsigned long durationMs) {
    if (!isRunning()) {
        turnOn();
        startTime = millis();
        runDuration = durationMs;
    }
}

void WaterPump::update() {
    if (isRunning() && (millis() - startTime >= runDuration)) {
        turnOff();
    }
}