#include <Arduino.h>

// Helligkeitssensor an Pin 28
const int sensorPin = 28;  

// LED an Pin 19
const int ledPin = 19;

// Referenzwert
const int normwert = 512;
const int abweichung = 300;
const int obereGrenze = normwert + abweichung;
const int untereGrenze = normwert - abweichung;

// PWM-Bereich für LED
const int pwmMax = 255;

void setup() {
    pinMode(ledPin, OUTPUT);
    Serial.begin(9600);
}

int rescale(int value, int in_min, int in_max, int out_min, int out_max) {
    return (value - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
}

void loop() {
    // 10-Bit-ADC-Wert lesen
    int messwert = analogRead(sensorPin);
    
    // Messwert auf der Konsole ausgeben
    Serial.println(messwert);

    if (messwert > obereGrenze) {
        analogWrite(ledPin, 0);
    } else if (messwert < untereGrenze) {
        analogWrite(ledPin, pwmMax);
    } else {
        int pwm_wert = pwmMax - rescale(messwert, untereGrenze, obereGrenze, 0, pwmMax);
        analogWrite(ledPin, pwm_wert);
    }
    delay(10);
}
