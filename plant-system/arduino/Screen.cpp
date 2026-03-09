#include "Screen.h"

Screen::Screen() {}

void Screen::begin() {
  tft.begin();
  tft.setRotation(3); 
  tft.fillScreen(TFT_BLACK);
  tft.setTextColor(TFT_GREEN, TFT_BLACK);
  tft.setTextSize(3);
}

void Screen::displayMoisture(int percent) {
  tft.fillScreen(TFT_BLACK);
  tft.setCursor(10, 40);
  tft.print("Moisture: ");
  tft.print(percent);
  tft.println("%");
}

void Screen::displayThreshold(int threshold) {
  tft.fillRect(0, 80, 320, 40, TFT_BLACK); 

  tft.setCursor(10, 120);
  tft.setTextColor(TFT_YELLOW, TFT_BLACK);
  tft.setTextSize(2);
  tft.print("Threshold: ");
  tft.print(threshold);
  tft.println("%");
}
