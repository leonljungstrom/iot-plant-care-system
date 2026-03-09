#include "UltraSonicSensor.h"
#include <Arduino.h>

UltraSonicSensor::UltraSonicSensor(int sigPin) : sigPin(sigPin){
    pinMode(sigPin, OUTPUT);
}

int UltraSonicSensor::getDistance() {
    
    pinMode(sigPin, OUTPUT);
    digitalWrite(sigPin, LOW);
    delayMicroseconds(2);
    digitalWrite(sigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(sigPin, LOW);

  // Listen for echo
    pinMode(sigPin, INPUT);

    duration = pulseIn(sigPin, HIGH, 30000);
    distance = (duration*.0343)/2;

    return distance;
}