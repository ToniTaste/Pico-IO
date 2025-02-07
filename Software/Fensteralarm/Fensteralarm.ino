#include <Arduino.h>

// Buzzer an Pin 15 über PWM anschließen
const int buzzer = 15;

// Analoger Sensor an Pin 27 einrichten
const int sensor = 27;

// Normwert als Referenz
const int normwert = 533;

void spiele_Ton(int frequency, int duration) {
    tone(buzzer, frequency);
    delay(duration);
    noTone(buzzer);
    delay(duration*5);
}

void setup() {
    pinMode(buzzer, OUTPUT);
}

void loop() {
    // Sensorwert einlesen
    int messwert = analogRead(sensor);


    // Prüfen, ob der Messwert nahe am Normwert liegt (Differenz < 25)
    if (abs(messwert - normwert) < 25) {
        spiele_Ton(1000, 50);
    }
}