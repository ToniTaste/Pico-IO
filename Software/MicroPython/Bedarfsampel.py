from machine import Pin
from time import sleep

# GPIO-Definitionen
led_s_rot = Pin(16, Pin.OUT)
led_s_gelb = Pin(17, Pin.OUT)
led_s_gruen = Pin(18, Pin.OUT)
led_f_rot = Pin(20, Pin.OUT)
led_f_gruen = Pin(22, Pin.OUT)
button = Pin(26, Pin.IN, Pin.PULL_DOWN)

def schaltePhase():
    # Phase 1: Warte, dann: Seite – Rot aus, Gelb an, Grün aus
    sleep(0.5)
    led_s_rot.off()
    led_s_gelb.on()
    led_s_gruen.off()

    # Phase 2: Warte, dann: Seite – Rot an, Gelb aus, Grün aus
    sleep(1)
    led_s_rot.on()
    led_s_gelb.off()
    led_s_gruen.off()

    # Phase 3: Warte, dann: Fußgänger – Rot aus, Grün an
    sleep(0.5)
    led_f_rot.off()
    led_f_gruen.on()

    # Phase 4: Warte, dann: Fußgänger – Rot an, Grün aus
    sleep(2)
    led_f_rot.on()
    led_f_gruen.off()

    # Phase 5: Warte, dann: Seite – Rot und Gelb an, Grün aus
    sleep(0.5)
    led_s_rot.on()
    led_s_gelb.on()
    led_s_gruen.off()

    # Phase 6: Warte, dann: Seite – Rot und Gelb aus, Grün an
    sleep(0.5)
    led_s_rot.off()
    led_s_gelb.off()
    led_s_gruen.on()

    # Abschließende Pause
    sleep(0.5)

def main():
    # Initiale Zustände
    led_s_rot.off()
    led_s_gelb.off()
    led_s_gruen.on()
    led_f_rot.on()
    led_f_gruen.off()
    
    while True:
        if button.value():
            schaltePhase()
            # Entprellung: Warten, bis der Taster wieder losgelassen wird
            while button.value():
                sleep(0.1)

if __name__ == "__main__":
    main()
