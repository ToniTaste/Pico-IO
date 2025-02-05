from machine import Pin, ADC, PWM
from time import sleep

# Buzzer an Pin 15 über PWM anschließen
buzzer = PWM(Pin(15))

# Analogen Sensor an Pin 27 einrichten
sensor = ADC(Pin(27))

# Normwert als Referenz
normwert = 32768

def play_buzzer(frequency, duration):
    buzzer.freq(frequency)
    buzzer.duty_u16(32768)  # 50% Duty Cycle
    sleep(duration)
    buzzer.duty_u16(0)  # Aus

while True:
    # Sensorwert einlesen
    messwert = sensor.read_u16()

    # Prüfen, ob der Messwert nahe am Normwert liegt (Differenz < 25)
    if abs(messwert - normwert) < 7000:
        play_buzzer(1000, 0.25)
        sleep(0.5)
