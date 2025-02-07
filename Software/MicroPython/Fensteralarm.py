from machine import Pin, ADC, PWM
from time import sleep

# Buzzer an Pin 15 über PWM anschließen
buzzer = PWM(Pin(15))

# Analogen Sensor an Pin 27 einrichten
sensor = ADC(Pin(27))

# Normwert als Referenz
normwert = 533 

def spiele_Ton(frequency, duration):
    buzzer.freq(frequency)
    buzzer.duty_u16(32768)  
    sleep(duration)
    buzzer.duty_u16(0)  # Aus

while True:
    # Sensorwert einlesen mit Anpassung von 16 Bit auf 10 Bit
    messwert = sensor.read_u16() >> 6 

    # Prüfen, ob der Messwert nahe am Normwert liegt (Differenz < 25)
    if abs(messwert - normwert) < 25:
        spiele_Ton(1000, 0.25)
        sleep(0.5)
