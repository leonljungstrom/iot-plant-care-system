#ifndef SOIL_SENSOR_H
#define SOIL_SENSOR_H

class SoilSensor {
private:
  int pin;
  int minValue;
  int maxValue;

public:
  SoilSensor(int analogPin, int dry = 1023, int wet = 761);
  int readRaw();
  int readPercent();
};

#endif
