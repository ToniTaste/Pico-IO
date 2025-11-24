import jssc.SerialPort;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.Pin;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PicoIO {
  //Hardware
  private FirmataDevice device;

  //globale Vereinbarungen
  private int hallBaseValue = 533;     // Startwert, wird überschrieben
  private int hallTolerance = 50;      // +/- Abweichung
  private int lightMin = 1023;         // wird durch Kalibrierung gesetzt
  private int lightMax = 0;            // wird durch Kalibrierung gesetzt

  private final int BUTTON_THRESHOLD = 500; // Schwellenwert fuer den analogen Button
  private static final int[] VALID_VIDS = {0x2E8A}; // offizieller Raspberry Pi Vendor ID
  private static final int[] VALID_PIDS = {0x00C0, 0x000A, 0x0003};

  //Ausgaben
  private final int[] ledPins = {16, 17, 18, 19, 20, 21, 22};
  private final int buzzerPin = 15;

  //Eingaben
  private final int buttonPin = 26;
  private final int hallSensorPin = 27;
  private final int lightSensorPin = 28;

  //Status
  private boolean boardActive = false;
  private boolean wasPressed = false;
  private final ConcurrentHashMap<Integer, Thread> loops = new ConcurrentHashMap<>(); // Speichert die Loops
  private final Map<String, Integer> activeLoopIdentifiers = new HashMap<>();
  private int loopCounter = 0;
  private long startZeit = -1L;

  /**
   * Konstruktor mit Selbsttest
   */
  public PicoIO() {
    // System.out.println("PicoIO Selbsttest...");
    // open();      // hier wird bereits initialisiert
    // close();     // sauber wieder schließen
    // System.out.println("PicoIO Selbsttest beendet.");
  }

  private void ensureConnected() {
    if (!boardActive) {
      System.out.println("Starte automatische Verbindung zum Pico...");
      open();
    }
  }

  /**
   * Hard-Reset für jSSC und die Pico-Verbindung.
   * Macht erneutes open() möglich, ohne JVM neu starten zu müssen.
   */
  public void hardReset() {
    System.out.println("Hard-Reset wird ausgeführt...");

    // 1. Alles sauber schließen
    try {
      stopAllLoops();
      if (device != null) {
        try {
          device.stop();
        } catch (Exception ignored) {}
      }
      device = null;
      boardActive = false;
    } catch (Exception e) {
      e.printStackTrace();
    }

    // 2. jSSC nativen Zustand zurücksetzen
    try {
      Class<?> cls = Class.forName("jssc.SerialNativeInterface");

      java.lang.reflect.Field field = cls.getDeclaredField("nativeLibraryLoaded");
      field.setAccessible(true);
      field.setBoolean(null, false);

      System.out.println("jSSC Native-Library-Status zurückgesetzt.");
    } catch (Exception e) {
      System.err.println("Hard-Reset (Stufe 2) nicht möglich: " + e.getMessage());
    }

    try {
      Class<?> cls2 = Class.forName("jssc.SerialPort");
      java.lang.reflect.Field openedPortsField = cls2.getDeclaredField("openedPorts");
      openedPortsField.setAccessible(true);
      HashMap<?, ?> map = (HashMap<?, ?>) openedPortsField.get(null);
      map.clear();

      System.out.println("jSSC Port-Liste geleert.");
    } catch (Exception e) {
      System.err.println("Hard-Reset (Stufe 2b) nicht möglich: " + e.getMessage());
    }

    System.out.println("Hard-Reset abgeschlossen. Neuer Verbindungsaufbau möglich.");
  }

  /**
   * Methode zum Verbinden mit dem PicoIO
   * Falls Probleme: 3x Neustart
   */
  public void open() {
    if (device != null) {
      System.err.println("Pico ist bereits verbunden.");
      return;
    }
    String port = findePicoPort();
    if (port == null) {
      System.err.println("Kein Raspberry Pi Pico gefunden! Stellen Sie sicher, dass der Pico angeschlossen ist und checken Sie die Berechtigungen für serielle Ports.");
      hardReset();
      return;
    }
    device = new FirmataDevice(port);
    try {
      device.start();
      long startTime = System.currentTimeMillis();
      while (!device.isReady()) {
        if (System.currentTimeMillis() - startTime > 3000) {
          throw new IOException("Timeout beim Initialisieren von Firmata.");
        }
        Thread.sleep(100);
      }
    } catch (InterruptedException | IOException e) {
      System.err.println("Fehler beim Starten von Firmata: " + e.getMessage());
      hardReset();
      return;
    }
    initializePins();
    boardActive = true;
    System.out.println("Pico verbunden auf: " + port);
    startButtonMonitor();
  }

  /**
   * Verbindung zu PicoIO beenden und alle Aktoren/Threads deaktivieren
   */
  public void close() {
    if (device == null) {
      System.err.println("Kein Pico verbunden.");
      return;
    }
    try {
      stopAllLoops();
      ledsOff();
      device.stop();
      device = null;
      boardActive = false;
      System.out.println("Verbindung zum Pico beendet.");
    } catch (IOException e) {
      System.err.println("Fehler beim Beenden der Verbindung.");
    }
  }

  private void startButtonMonitor() {
    Thread buttonThread = new Thread(() -> {
            while (boardActive) {
              try {
                if (device.getPin(buttonPin).getValue() > BUTTON_THRESHOLD) {
                  wasPressed = true;
                }
                Thread.sleep(100);
              } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
              }
            }
        });
    buttonThread.setDaemon(true);
    buttonThread.start();
  }

  private boolean isPicoByUsbId(com.fazecast.jSerialComm.SerialPort port) {
    int vid = port.getVendorID();
    int pid = port.getProductID();

    System.out.printf("USB-ID gefunden: VID=0x%04X, PID=0x%04X%n", vid, pid);

    // VID prüfen
    if (vid != 0x2E8A) return false;

    // PID gegen Whitelist prüfen
    for (int allowed : VALID_PIDS) {
      if (pid == allowed) {
        return true;
      }
    }

    return false;
  }

  private String findePicoPort() {
    String os = System.getProperty("os.name").toLowerCase();

    com.fazecast.jSerialComm.SerialPort[] ports = com.fazecast.jSerialComm.SerialPort.getCommPorts();

    for (com.fazecast.jSerialComm.SerialPort port : ports) {
      String systemPortName = port.getSystemPortName();

      // USB-ID prüfen
      if (isPicoByUsbId(port)) {
        System.out.println("USB-ID passt → potentieller Pico: " + systemPortName);

        // Zusätzlicher Firmata-Test
        if (isPicoDevice(systemPortName)) {
          System.out.println("Firmata bestätigt → Pico gefunden");
          return systemPortName;
        } else {
          System.out.println("Firmata nicht erkannt – falscher Modus?");
        }
      }
    }

    return null;
  }

  private boolean isPicoDevice(String portName) {
    //SerialPort port = new SerialPort(portName);
    jssc.SerialPort port = new jssc.SerialPort(portName);
    try {
      port.openPort();
      port.setParams(57600, 8, 1, 0); // Standard-Firmata

      // Firmata REPORT_VERSION anfordern
      port.writeBytes(new byte[]{(byte) 0xF9});
      Thread.sleep(300);

      byte[] buffer = port.readBytes();
      port.closePort();

      if (buffer != null) {
        for (byte b : buffer) {
          if (b == (byte) 0xF9) {
            return true; // Firmata erkannt
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Fehler beim Testen von " + portName + ": " + e.getMessage());
    }
    return false;
  }

  private void initializePins() {
    try {
      for (int pin : ledPins) {
        device.getPin(pin).setMode(Pin.Mode.PWM);
      }
      device.getPin(buttonPin).setMode(Pin.Mode.ANALOG);
      device.getPin(hallSensorPin).setMode(Pin.Mode.ANALOG);
      device.getPin(lightSensorPin).setMode(Pin.Mode.ANALOG);
      device.getPin(buzzerPin).setMode(Pin.Mode.PWM);
    } catch (IOException e) {
      System.err.println("Fehler beim Initialisieren der Pins: " + e.getMessage());
    }
  }

  /**
   * Methode zum Kalibireren des HALL-Sensors (kein Magnet in der Nähe)
   */
  public void calibrateHall() {
    ensureConnected();
    System.out.println("Kalibriere Hallsensor...");

    int samples = 100;
    int sum = 0;

    for (int i = 0; i < samples; i++) {
      sum += (int) device.getPin(hallSensorPin).getValue();
      pause(10);
    }

    hallBaseValue = sum / samples;

    System.out.println("Hall-Basiswert: " + hallBaseValue);
  }

  /**
   * Methode zum Kalibrieren des Lichtsensors auf Mittelwert
   */
  public void calibrateLight() {
    ensureConnected();
    System.out.println("Kalibriere Helligkeitssensor...");

    int samples = 200;
    lightMin = 1023;
    lightMax = 0;

    for (int i = 0; i < samples; i++) {
      int val = (int) device.getPin(lightSensorPin).getValue();
      if (val < lightMin) lightMin = val;
      if (val > lightMax) lightMax = val;
      pause(5);
    }

    System.out.println("Licht minimal: " + lightMin);
    System.out.println("Licht maximal: " + lightMax);
  }

  /**
   * Methode zur Rückgabe des Mittelwerts der Helligkeit
   * @return Mittelwert
   */
  public double getLightNormalized() {
    ensureConnected();
    int val = (int) device.getPin(lightSensorPin).getValue();
    if (lightMax == lightMin) return 0; // Schutz
    double norm = (val - lightMin) / (double)(lightMax - lightMin);
    return Math.max(0, Math.min(1, norm));
  }

  private boolean isValidLEDPin(int pin) {
    for (int ledPin : ledPins) {
      if (ledPin == pin) return true;
    }
    return false;
  }

  /**
   * Boardstatus abfragen
   * @return ture/false Board vorhanden/fehlt
   */
  public boolean getStatus() {
    ensureConnected();
    return boardActive;
  }

  /**
   * LED einschalten
   * @param pin Nummer der LED (16..22)
   */
  public void ledOn(int pin){
    ensureConnected();
    ledDim(pin, 255);
  }

  /**
   * LED ausschalten
   * @param pin Nummer der LED (16..22)
   */
  public void ledOff(int pin)  {
    ensureConnected();
    ledDim(pin, 0);
  }

  /**
   * LED umschalten, falls Dimmung ausschalten
   * @param pin Nummer der LED (16..22)
   */
  public void ledSwitch(int pin) {
    ensureConnected();
    if (isValidLEDPin(pin)) {
      ledDim(pin, getLedValue(pin) == 0 ? 255 : 0); // Wechsel zum alten Wert
    }
  }

  /**
   * LED schalten
   * @param pin Nummer der LED (16..22)
   * @param state true/false  ein/aus
   */
  public void ledSet(int pin, boolean state)  {
    ensureConnected();
    ledDim(pin, state ? 255 : 0);
  }

  /**
   * LED dimmen
   * @param pin Nummer der LED (16..22)
   * @param value Helligkeit (0..255)
   */
  public void ledDim(int pin, int value) {
    ensureConnected();
    try {
      if (isValidLEDPin(pin)) {
        device.getPin(pin).setValue(Math.max(0, Math.min(255, value)));
      }
    } catch (IOException e) {
      System.err.println("Fehler beim Dimmen der LED: " + e.getMessage());
    }
  }

  /**
   * Abfrage LED-Wert
   * @param pin Nummer der LED (16..22)
   * @return Helligkeitswert (0..255)
   */
  public int getLedValue(int pin) {
    ensureConnected();
    return isValidLEDPin(pin) ? (int) device.getPin(pin).getValue() : -1;
  }

  /**
   * Abfrage LED-Zustand
   * @param pin Nummer der LED (16..22)
   * @return Zustand true/false ein/aus
   */
  public boolean getLedState(int pin) {
    ensureConnected();
    return getLedValue(pin) > 0;
  }

  /**
   * alle LEDs ausschalten
   */
  public void ledsOff()  {
    ensureConnected();
    for (int pin : ledPins) {
      ledOff(pin);
    }
  }

  /**
   * Abfrage Tasterzustand aktuell
   * @return true/false geschlossen/offen
   */
  public boolean isPressed() {
    ensureConnected();
    return device.getPin(buttonPin).getValue() > BUTTON_THRESHOLD;
  }

  /**
   * Abfrage Tasterzustand seit letzter Abfrage
   * @return true/false geschlossen/offen
   */
  public boolean wasPressed() {
    ensureConnected();
    boolean state = wasPressed;
    wasPressed = false;
    return state;
  }

  /**
   * Tasterzustand bereinigen
   */
  public void clearPressed() {
    ensureConnected();
    wasPressed();
  }

  /**
   * Abfrage Helligkeitssensor
   * @return Helligkeit (0..1023)
   */
  public int getLight() {
    ensureConnected();
    return (int) device.getPin(lightSensorPin).getValue();
  }

  /**
   * Abfrage Hallssensor (Magnetfeld)
   * @return Hallwert (0..1023)
   */
  public int getHall()  {
    ensureConnected();
    return (int) device.getPin(hallSensorPin).getValue();
  }

  /**
   * Abfrage Hallssensor (Magnetanwesenheit)
   * @return true/false Magnet/kein Magnet
   */
  public boolean isContacted() {
    ensureConnected();
    int e = (int) device.getPin(hallSensorPin).getValue();
    return (e < hallBaseValue - hallTolerance) || (e > hallBaseValue + hallTolerance);
  }

  /**
   * Tonausgabe
   * @param duration Dauer in ms (max. 5 Sekunden)
   */
  public void playBeep(int duration)  {
    ensureConnected();
    int pwmValue = 200;
    if (duration > 5000 ||duration < 0){
      System.err.println("Fehler bei der Tonausgabe: Dauer zu lang");
    }
    else{
      try {
        device.getPin(buzzerPin).setValue(pwmValue);
        Thread.sleep(duration);
        device.getPin(buzzerPin).setValue(0);  // Ton stoppen
      } catch (IOException | InterruptedException e) {
        System.err.println("Fehler bei der Tonausgabe: " + e.getMessage());
      }
    }
  }

  /**
   * Tonausgabe stoppen
   */
  public void stopBeep()  {
    ensureConnected();
    try {
      device.getPin(buzzerPin).setValue(0);
    } catch (IOException e) {
      System.err.println("Fehler bei der Tonausgabe: " + e.getMessage());
    }
  }

  /**
   * Start einer Uhr
   */
  public void startClock() {
    ensureConnected();
    startZeit = System.currentTimeMillis();
  }

  /**
   * Abfrage Laufzeit der Uhr
   * @return Laufzeit
   */
  public long getClock() {
    ensureConnected();
    if (startZeit == -1L) {
      System.out.println("Die Stoppuhr wurde nicht gestartet!");
      return 0L;
    }
    return System.currentTimeMillis() - startZeit;
  }

  /**
   * Stoppen der Uhr und Abfrage der Laufzeit
   * @return Laufzeit
   */
  public long stopClock() {
    ensureConnected();
    long elapsedTime = getClock();
    startZeit = -1L; // Reset
    return elapsedTime;
  }

  /**
   * Programm pausiert
   * @param milliseconds Zeit in ms
   */
  public void pause(int milliseconds) {
    ensureConnected();
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // Interrupt-Status beibehalten
    }
  }

  /**
   * Wiederholter Durchlauf einer Methode als Hintergrundprozess
   * @param object Objekt
   * @param methodName Name der Methode
   * @return Prozessnummer
   */
  public int startLoop(Object object, String methodName) {
    ensureConnected();
    return startLoopWithDelay(object, methodName, 0);
  }

  /**
   * Wiederholter Durchlauf einer Methode als Hintergrundprozess mit Zeitabstand
   * @param object Objekt
   * @param methodName Name der Methode
   * @param delay Zeitabstand
   * @return Prozessnummer
   */
  public int startLoopWithDelay(Object object, String methodName, int delay) {
    ensureConnected();
    String loopKey = object.getClass().getName() + "#" + methodName;
    if (activeLoopIdentifiers.containsKey(loopKey)) {
      System.err.println("Loop der Methode " + loopKey + " bereits aktiv - kein erneuter Start!");
      return -1;
    }
    int loopId = loopCounter++;
    Thread thread = new Thread(() -> {
            try {
              java.lang.reflect.Method method = object.getClass().getDeclaredMethod(methodName);
              method.setAccessible(true); // Zugriff auf private Methoden erlauben
              while (!Thread.currentThread().isInterrupted()) { // Korrekte Interrupt-Prüfung
                method.invoke(object);
                Thread.sleep(delay);
              }
            } catch (InterruptedException e) {
              System.out.println("Loop " + loopId + " wurde durch Interrupt beendet.");
              Thread.currentThread().interrupt();
            } catch (Exception e) {
              if (e.getCause() instanceof InterruptedException) { // Falls invoke() InterruptedException auslöst
                System.out.println("Loop " + loopId + " wurde durch Methode unterbrochen.");
                Thread.currentThread().interrupt();
              } else {
                System.err.println("Fehler in Loop " + loopId + ": " + e.getMessage());
              }
            }
        });
    loops.put(loopId, thread);
    activeLoopIdentifiers.put(loopKey, loopId);
    thread.start();
    return loopId;
  }

  /**
   * Hintergrundprozess beenden
   * @param loopId Prozessnummer
   */
  public void stopLoop(int loopId) {
    ensureConnected();
    Thread thread = loops.get(loopId); // Thread nicht sofort entfernen
    if (thread != null) {
      thread.interrupt();
      try {
        thread.join(2000); // Max. 2 Sekunden warten, bis der Thread beendet ist
        if (thread.isAlive()) {
          System.err.println("Thread " + loopId + " wurde nicht korrekt beendet!");
        }
      } catch (InterruptedException e) {
        System.err.println("Fehler beim Warten auf das Beenden des Loops: " + e.getMessage());
        Thread.currentThread().interrupt();
      }
      loops.remove(loopId); // Immer aus der Map entfernen
      activeLoopIdentifiers.values().removeIf(id -> id == loopId);
    }
  }

  /**
   * Alle Hintergrundprozesse beenden
   */
  public void stopAllLoops() {
    ensureConnected();
    Set<Integer> loopIds = new HashSet<>(loops.keySet());
    for (int loopId : loopIds) {
      stopLoop(loopId);
    }
    loops.clear();
    activeLoopIdentifiers.clear();
  }

  /**
   * Ausgabe der Hintergrundprozessnummern auf Konsole
   */
  public void listLoops() {
    ensureConnected();
    System.out.println("Aktive Loops: " + loops.keySet());
  }

}
