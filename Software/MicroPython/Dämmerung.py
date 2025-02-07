from machine import Pin, ADC, PWM
# Besonderheit:
# Der Raspi Pico hat eine 12 Bit ADC, die mit read_u16 als 16 Bit Zahl übergeben wird
# Alle blockbasierten Systeme arbeiten nutzen/konvertieren 10 Bit ADC


# Helligkeitssensor an Pin 28
sensor = ADC(Pin(28))

# LED Pin 19
gpio = 19
led = PWM(Pin(gpio))
led.freq(1000)

# Referenzwert
normwert = 512  # Mittlerer Helligkeitswert (bei 10 Bit ADC)
abweichung = 300
obereGrenze = normwert + abweichung
untereGrenze = normwert - abweichung

#Skaliert einen Wert von einem Eingangsbereich in einen Ausgangsbereich.
def rescale(value, in_min, in_max, out_min, out_max):
    return int((value - in_min) * (out_max - out_min) / (in_max - in_min) + out_min)

while True:
    # 16-Bit auf 10-Bit umwandeln
    messwert = sensor.read_u16() >> 6
    
   if messwert > obereGrenze:
        led.duty_u16(0)  
    elif messwert < untereGrenze:
        led.duty_u16(65535)
    else:
        # Skaliere den Messwert im Grenzintervall auf [0, 65535]
        pwm_wert = 65535 - rescale(messwert, untereGrenze, obereGrenze, 0, 65535)
        led.duty_u16(pwm_wert)
