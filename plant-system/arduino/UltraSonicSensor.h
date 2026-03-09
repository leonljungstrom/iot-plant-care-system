#ifndef ULTRA_SONIC_SENSOR_H
#define ULTRA_SONIC_SENSOR_H

class UltraSonicSensor {
public:
    const int sigPin;
    float duration;
    float distance;
    UltraSonicSensor(int sigPin);
    int getDistance();
};

#endif