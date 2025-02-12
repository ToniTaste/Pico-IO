import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PicoIO {
  //Hardware
  private static FirmataDevice device;

  //globale Vereinbarungen
  private static final int BUTTON_THRESHOLD = 500; // Schwellenwert fuer den analogen Button
  private static final int MAGNETIC_THRESOLD = 533; // Normwert fuer den Magnetsensor

  //Ausgaben
  private static final int[] ledPins = {16, 17, 18, 19, 20, 21, 22};
  private static final int buzzerPin = 15;

  //Eingaben
  private static final int buttonPin = 26;
  private static final int hallSensorPin = 27;
  private static final int lightSensorPin = 28;

  //Status
  private static boolean boardActive = false;
  private static boolean wasPressed = false;
  private static final ConcurrentHashMap<Integer, Thread> loops = new ConcurrentHashMap<>(); // Speichert die Loops
  private static final Map<String, Integer> activeLoopIdentifiers = new HashMap<>();
  private static int loopCounter = 0;
  private static long startZeit;

  // Statische Initialisierung anstelle des Konstruktors
  static {
    open();
    initializePins();
    close();
  }

  /**
   * Methode zum Verbinden mit dem PicoIO
   * Falls Probleme: 3x Neustart der JVM
   */
  public static void open() {
    if (device != null) {
      System.err.println("Pico ist bereits verbunden.");
      return;
    }
    String port = findPicoPort();
    if (port == null) {
      restartJVM("Kein Raspberry Pi Pico gefunden!");
    }
    device = new FirmataDevice(port);
    int attempts = 3;
    while (attempts > 0) {
      try {
        device.start();
        break;
      } catch (IOException e) {
        attempts--;
        System.err.println("Fehler beim Starten von Firmata. Verbleibende Versuche: " + attempts);
        if (attempts == 0) {
          restartJVM("Fehlgeschlagen nach 3 Versuchen.");
        }
      }
    }

    try {
      long startTime = System.currentTimeMillis();
      while (!device.isReady()) {
        if (System.currentTimeMillis() - startTime > 3000) {
          throw new IOException("Timeout beim Initialisieren von Firmata.");
        }
        Thread.sleep(100);
      }
    } catch (InterruptedException | IOException e) {
      restartJVM("Fehler beim Warten auf Firmata.");
    }

    initializePins();
    boardActive = true;
    System.out.println("Pico verbunden auf: " + port);
    startZeit = -1L;
    // Starte den Hintergrund-Thread fuer die Taster-Abfrage
    startButtonMonitor();
  }

  /** 
   * Verbindung zu PicoIO beenden und alle Aktoren/Threads deaktivieren 
   */
  public static void close() {
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

  private static void startButtonMonitor() {
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

  private PicoIO() {} // Verhindert Instanziierung

  private static String findPicoPort() {
    SerialPort[] ports = SerialPort.getCommPorts();
    for (SerialPort port : ports) {
      if (port.getPortDescription().toLowerCase().contains("pico")) {
        return port.getSystemPortName();
      }
    }
    return null;
  }

  private static void initializePins() {
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

  private static void restartJVM(String reason) {
    System.err.println("Fehler: " + reason + " - Neustart der JVM...");
    try {
      String javaBin = System.getProperty("java.home") + "/bin/java";
      String[] command = {javaBin, "-cp", System.getProperty("java.class.path"), PicoIO.class.getName()};
      new ProcessBuilder(command).start();
      System.exit(0);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static boolean isValidLEDPin(int pin) {
    for (int ledPin : ledPins) {
      if (ledPin == pin) return true;
    }
    return false;
  }

  /** 
   * Boardstatus abfragen
   * @return ture/false Board vorhanden/fehlt
   */
  public static boolean getStatus() {
    return boardActive;
  }

  /**
   * LED einschalten
   * @param pin Nummer der LED (16..22)
   */
  public static void ledOn(int pin){
    ledDim(pin, 255);
  }

  /**
   * LED ausschalten
   * @param pin Nummer der LED (16..22)
   */
  public static void ledOff(int pin)  {
    ledDim(pin, 0);
  }

  /**
   * LED umschalten, falls Dimmung ausschalten
   * @param pin Nummer der LED (16..22)
   */
  public static void ledSwitch(int pin) {
    if (isValidLEDPin(pin)) {
      ledDim(pin, getLedValue(pin) == 0 ? 255 : 0); // Wechsel zum alten Wert
    }
  }

  /**
   * LED schalten
   * @param pin Nummer der LED (16..22)
   * @param state true/false  ein/aus
   */
  public static void ledSet(int pin, boolean state)  {
    ledDim(pin, state ? 255 : 0);
  }

  /**
   * LED dimmen
   * @param pin Nummer der LED (16..22)
   * @param value Helligkeit (0..255)
   */
  public static void ledDim(int pin, int value) {
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
  public static int getLedValue(int pin) {
    return isValidLEDPin(pin) ? (int) device.getPin(pin).getValue() : -1;
  }

  /**
   * Abfrage LED-Zustand
   * @param pin Nummer der LED (16..22)
   * @return Zustand true/false ein/aus
   */
  public static boolean getLedState(int pin) {
    return getLedValue(pin) > 0;
  }

  /**
   * alle LEDs ausschalten
   */
  public static void ledsOff()  {
    for (int pin : ledPins) {
      ledOff(pin);
    }
  } 

  /**
   * Abfrage Tasterzustand aktuell 
   * @return true/false geschlossen/offen
   */
  public static boolean isPressed() {
    return device.getPin(buttonPin).getValue() > BUTTON_THRESHOLD;
  }

  /**
   * Abfrage Tasterzustand seit letzter Abfrage 
   * @return true/false geschlossen/offen
   */
  public static boolean wasPressed() {
    boolean state = wasPressed;
    wasPressed = false;
    return state;
  }

  /**
   * Tasterzustand bereinigen
   */
  public static void clearPressed() {
    wasPressed();
  }

  /**
   * Abfrage Helligkeitssensor 
   * @return Helligkeit (0..1023)
   */
  public static int getLight() {
    return (int) device.getPin(lightSensorPin).getValue();
  }

  /**
   * Abfrage Hallssensor (Magnetfeld)
   * @return Hallwert (0..1023)
   */
  public static int getHall()  {
    return (int) device.getPin(hallSensorPin).getValue();
  }

  /**
   * Abfrage Hallssensor (Magnetanwesenheit)
   * @return true/false Magnet/kein Magnet
   */
  public static boolean isContacted()  {
    int e = getHall();
    return (e < MAGNETIC_THRESOLD-50 || e > MAGNETIC_THRESOLD+50);
  }

  /**
   * Tonausgabe
   * @param duration Dauer in ms (max. 5 Sekunden)
   */
  public static void playBeep(int duration)  {
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
  public static void stopBeep()  {
    try {
      device.getPin(buzzerPin).setValue(0);
    } catch (IOException e) {
      System.err.println("Fehler bei der Tonausgabe: " + e.getMessage());
    }
  }

  /**
   * Start einer Uhr
   */
  public static void startClock() {
    startZeit = System.nanoTime();
  }

  /**
   * Abfrage Laufzeit der Uhr
   * @return Laufzeit
   */
  public static long getClock() {
    if (startZeit == -1L) {
      System.out.println("Die Stoppuhr wurde nicht gestartet!");
      return 0L;
    }
    long elapsedTime = (System.nanoTime() - startZeit) / 1_000_000L;
    return elapsedTime;
  }

  /**
   * Stoppen der Uhr und Abfrage der Laufzeit
   * @return Laufzeit
   */
  public static long stopClock() {
    long elapsedTime = getClock();
    startZeit = -1L; // Reset 
    return elapsedTime;
  }

  /**
   * Programm pausiert
   * @param milliseconds Zeit in ms
   */
  public static void pause(int milliseconds) {
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
  public static int startLoop(Object object, String methodName) {
    return startLoopWithDelay(object, methodName, 0);
  }

  /**
   * Wiederholter Durchlauf einer Methode als Hintergrundprozess mit Zeitabstand
   * @param object Objekt
   * @param methodName Name der Methode
   * @param delay Zeitabstand
   * @return Prozessnummer
   */
  public static int startLoopWithDelay(Object object, String methodName, int delay) {
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
  public static void stopLoop(int loopId) {
    Thread thread = loops.get(loopId); // Thread nicht sofort entfernen
    if (thread != null) {
      thread.interrupt();
      try {
        thread.join(2000); // Max. 2 Sekunden warten, bis der Thread beendet ist
        if (thread.isAlive()) {
          System.err.println("⚠ Thread " + loopId + " wurde nicht korrekt beendet!");
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
  public static void stopAllLoops() {
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
  public static void listLoops() {
    System.out.println("Aktive Loops: " + loops.keySet());
  }

}
