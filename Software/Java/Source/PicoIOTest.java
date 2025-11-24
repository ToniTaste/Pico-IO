public class PicoIOTest {

  public static void main(String[] args) {

    PicoIO io = new PicoIO();

    System.out.println("\n==== PicoIO Testprogramm ====\n");

    // -------------------------------------------------------
    // 1. Verbindung prüfen
    // -------------------------------------------------------
    if (!io.getStatus()) {
      System.out.println("Keine aktive Verbindung. Versuche erneut...");
      io.open();
    }

    if (!io.getStatus()) {
      System.out.println("Pico konnte nicht verbunden werden. Test abgebrochen.");
      return;
    }

    System.out.println("Pico ist verbunden.\n");

    // -------------------------------------------------------
    // 2. LEDs testen
    // -------------------------------------------------------
    System.out.println("LED-Test (Pins 16–22):");
    for (int pin = 16; pin <= 22; pin++) {
      System.out.println("  LED an Pin " + pin + " → AN");
      io.ledOn(pin);
      io.pause(200);
      System.out.println("  LED an Pin " + pin + " → AUS");
      io.ledOff(pin);
      io.pause(200);
    }
    System.out.println("LED-Test abgeschlossen.\n");

    // -------------------------------------------------------
    // 3. Buzzer-Test
    // -------------------------------------------------------
    System.out.println("Buzzer-Test:");
    io.playBeep(300);
    io.pause(300);
    io.playBeep(300);
    io.stopBeep();
    System.out.println("Buzzer-Test abgeschlossen.\n");

    // -------------------------------------------------------
    // 4. Sensorwerte testen (ohne Kalibrierung)
    // -------------------------------------------------------
    System.out.println("Sensorwerte (roh):");
    System.out.println("  Licht:  " + io.getLight());
    System.out.println("  Hall:   " + io.getHall());
    System.out.println("  Button: " + io.isPressed());
    System.out.println();

    // -------------------------------------------------------
    // 5. Kalibrierung testen
    // -------------------------------------------------------
    System.out.println("Kalibriere Hallsensor...");
    io.calibrateHall();
    io.pause(500);

    System.out.println("Kalibriere Lichtsensor...");
    io.calibrateLight();
    io.pause(500);

    System.out.println("Kalibrierte Werte:");
    System.out.println("  Licht normalisiert: " + io.getLightNormalized());
    System.out.println("  Magnet erkannt:      " + io.isContacted());
    System.out.println();

    // -------------------------------------------------------
    // 6. Button-Abfrage (mit wasPressed)
    // -------------------------------------------------------
    System.out.println("Teste Button. Drücken Sie jetzt kurz den Taster...");

    boolean gedrueckt = false;
    long t0 = System.currentTimeMillis();

    while (System.currentTimeMillis() - t0 < 4000) {
      if (io.wasPressed()) {
        gedrueckt = true;
        break;
      }
      io.pause(100);
    }

    System.out.println("Button wurde gedrückt? → " + gedrueckt + "\n");

    // -------------------------------------------------------
    // 7. Hard-Reset testen
    // -------------------------------------------------------
    System.out.println("Hard-Reset wird getestet...");
    io.hardReset();
    io.pause(500);

    System.out.println("Versuche erneutes Verbinden...");
    io.open();
    System.out.println("Status nach reconnect: " + io.getStatus() + "\n");

    // -------------------------------------------------------
    // 8. Test abgeschlossen
    // -------------------------------------------------------
    System.out.println("==== Test abgeschlossen. ====");

    io.close();
  }
}
