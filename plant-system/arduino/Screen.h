#ifndef SCREEN_H
#define SCREEN_H

#include <TFT_eSPI.h>

class Screen {
private:
  TFT_eSPI tft;

public:
  Screen();
  void begin();
  void displayMoisture(int percent);
  void displayThreshold(int threshold);
};

#endif
