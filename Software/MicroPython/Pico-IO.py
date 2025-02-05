from machine import Pin, PWM, ADC
from time import sleep
import random

# GPIO Definitionen
buzzer = PWM(Pin(15))
led_pins = [Pin(i, Pin.OUT) for i in range(16, 23)]
middle_led_gpio = 19
button = Pin(26, Pin.IN, Pin.PULL_DOWN)
hall_sensor = ADC(Pin(27))
light_sensor = ADC(Pin(28))

# Konstanten
FADE_DELAY = 0.02
THRESHOLD_LIGHT = 8192   # Helligkeitsschwelle
HALL_STANDARD = 32768    # Hall-Regelwert

# Funktionen

def play_buzzer(frequency, duration):
    buzzer.freq(frequency)
    buzzer.duty_u16(32768)  # 50% Duty Cycle
    sleep(duration)
    buzzer.duty_u16(0)  # Aus

def blink_leds():
    for led in led_pins:
        led.on()
        sleep(0.2)
    for led in reversed(led_pins):
        led.off()
        sleep(0.2)

def fade_led(gpio):
    led_pwm = PWM(Pin(gpio))
    led_pwm.freq(1000)
    for duty in range(0, 65536, 1024):
        led_pwm.duty_u16(duty)
        sleep(FADE_DELAY)
    for duty in range(65536, 0, -1024):
        led_pwm.duty_u16(duty)
        sleep(FADE_DELAY)
        
    led_pwm.deinit()
    
    # Onboard-LED ausschalten
    Pin(gpio).init(Pin.OUT)


def display_dice(number):
    led_patterns = [
        [0, 0, 0, 0, 0, 0, 0],  # 0
        [0, 0, 0, 1, 0, 0, 0],  # 1
        [0, 0, 1, 0, 1, 0, 0],  # 2
        [1, 0, 0, 1, 0, 0, 1],  # 3
        [1, 0, 1, 0, 1, 0, 1],  # 4
        [1, 0, 1, 1, 1, 0, 1],  # 5
        [1, 1, 1, 0, 1, 1, 1]   # 6
    ]
    pattern = led_patterns[number]
    for led, state in zip(led_pins, pattern):
        led.value(state)

def main():
    # Zufallsgenerator initialisieren
    random.seed()
    
    # Ton-Ausgabe zu Beginn
    play_buzzer(1000, 0.5)

    # LEDs nacheinander blinken
    blink_leds()

    # Fade-In und Fade-Out der mittleren LED
    fade_led(middle_led_gpio)

    # Zurücksetzen der mittleren LED auf normalen GPIO-Betrieb (falls benötigt)
    #reset_pin(middle_led, Pin(19))

    while True:
    
        # Licht-Sensor-Warnsignal
        light_value = light_sensor.read_u16()
        print(str(light_value))
        if light_value < THRESHOLD_LIGHT:
            play_buzzer(500, 0.2)
            sleep(0.5)
        elif (button.value()):
            # Hall-Sensor prüfen
            hall_value = hall_sensor.read_u16()
            if hall_value < HALL_STANDARD * 0.9 or hall_value > HALL_STANDARD * 1.1:
                display_dice(6)

            # Taster zum "Würfeln"
            else:
                number = random.randint(1, 6)
                display_dice(number)
                
            sleep(1)  # Kurz warten, um Flackern zu vermeiden
            display_dice(0)

if __name__ == "__main__":
    main()
