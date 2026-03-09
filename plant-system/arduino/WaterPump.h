#ifndef WATER_PUMP_H
#define WATER_PUMP_H

class WaterPump {
private:
    int pin;
    bool isOn;
    unsigned long runDuration = 0;
    unsigned long startTime = 0;
public:
    WaterPump(int pin);
    void begin();
    void turnOn();
    void turnOff();
    bool isRunning();
    void runForMilliseconds(unsigned long durationMs);
    void update();
};

#endif
