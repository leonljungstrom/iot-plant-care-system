#include "SoilSensor.h"
#include <Arduino.h>

SoilSensor::SoilSensor(int analogPin, int dry, int wet) {
  pin = analogPin;
  minValue = dry;
  maxValue = wet;
}

int SoilSensor::readRaw() {
  return analogRead(pin);
}

int SoilSensor::readPercent() {
  int raw = readRaw();
  int percent = map(raw, minValue, maxValue, 0, 100);
  percent = constrain(percent, 0, 100);
  return percent;
}
