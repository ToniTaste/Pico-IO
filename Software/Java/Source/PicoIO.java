import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PicoIO {
  private static FirmataDevice device;
  private static final int[] ledPins = {16, 17, 18, 19, 20, 21, 22};
  private static final int buttonPin = 26;
  private static final int hallSensorPin = 27;
  private static final int lightSensorPin = 28;
  private static final int buzzerPin = 15;
  private static boolean boardActive = false;
  private static boolean wasPressed = false;
  private static final HashMap<Integer, Boolean> ledStates = new HashMap<>();
  private static final ConcurrentHashMap<Integer, Thread> loops = new ConcurrentHashMap<>();
  private static final Map<String, Integer> activeLoopIdentifiers = new HashMap<>();
  private static int loopCounter = 0;
  private static final int BUTTON_THRESHOLD = 500; // Schwellenwert fuer den analogen Button
  private static final int MAGNETIC_THRESOLD = 533; // Normwert fuer den Magnetsensor
  private static long startZeit;

  // Statische Initialisierung
  static {
    open();
    close();
  }

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
        device.getPin(pin).setMode(Pin.Mode.OUTPUT);
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

  public static void ledOn(int pin){
    try {
      if (isValidLEDPin(pin)) {
        device.getPin(pin).setValue(1);
        ledStates.put(pin, true);
      }
    } catch (IOException e) {
      System.err.println("Fehler beim Schalten der Pins: " + e.getMessage());
    }
  }

  public static void ledOff(int pin)  {
    try {
      if (isValidLEDPin(pin)) {
        device.getPin(pin).setValue(0);
        ledStates.put(pin, false);
      }
    } catch (IOException e) {
      System.err.println("Fehler beim Schalten der Pins: " + e.getMessage());
    }
  }

  public static void ledSwitch(int pin) {
    if (isValidLEDPin(pin)) {
      boolean currentState = ledStates.get(pin);
      ledSet(pin, !currentState);
    }
  } 

  public static void ledSet(int pin, boolean state)  {
    try {
      if (isValidLEDPin(pin)) {
        device.getPin(pin).setValue(state ? 1 : 0);
        ledStates.put(pin, state);
      }
    } catch (IOException e) {
      System.err.println("Fehler beim Schalten der Pins: " + e.getMessage());
    }
  }

  public static void ledsOff()  {
    for (int pin : ledPins) {
      ledOff(pin);
    }
  } 

  public static boolean isPressed() {
    return device.getPin(buttonPin).getValue() > BUTTON_THRESHOLD;
  }

  public static boolean wasPressed() {
    boolean state = wasPressed;
    wasPressed = false;
    return state;
  }

  public static void clearPressed() {
    wasPressed();
  }

  public static int getLight() {
    return (int) device.getPin(lightSensorPin).getValue();
  }

  public static int getHall()  {
    return (int) device.getPin(hallSensorPin).getValue();
  }

  public static boolean isContacted()  {
    long e = device.getPin(hallSensorPin).getValue();
    return (e < MAGNETIC_THRESOLD-50 || e > MAGNETIC_THRESOLD+50);
  }

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

  public static void stopBeep()  {
    try {
      device.getPin(buzzerPin).setValue(0);
    } catch (IOException e) {
      System.err.println("Fehler bei der Tonausgabe: " + e.getMessage());
    }
  }

  public static boolean getStatus() {
    return boardActive;
  }

  public static void pause(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException ignored) {}
  }

  public static void startClock() {
    startZeit = System.nanoTime();
  }

  public static long stopClock() {
    if (startZeit == -1L) {
      System.out.println("Die Stoppuhr wurde nicht gestartet!");
      return 0L;
    }
    long elapsedTime = (System.nanoTime() - startZeit) / 1_000_000L;
    startZeit = -1L; // Reset für kommende Messung
    return elapsedTime;
  }

  public static int startLoop(Object object, String methodName) {
    return startLoopWithDelay(object, methodName, 0);
  }

  public static void stopLoop(int loopId) {
    Thread thread = loops.remove(loopId);
    if (thread != null) {
      thread.interrupt();
      activeLoopIdentifiers.values().removeIf(id -> id == loopId);
    }
  }

  public static int startLoopWithDelay(Object object, String methodName, int delay) {
    String loopKey = object.getClass().getName() + "#" + methodName;
    if (activeLoopIdentifiers.containsKey(loopKey)) {
      System.err.println("Loop der Methode " + loopKey + " bereits aktiv - kein erneuter Start!");
      return -1;
    }
    int loopId = loopCounter++;
    Thread thread = new Thread(() -> {
            try {
              while (!Thread.currentThread().isInterrupted()) {
                object.getClass().getMethod(methodName).invoke(object);
                Thread.sleep(delay);
              }
            } catch (Exception e) {
              System.err.println("Fehler in Loop " + loopId + ": " + e.getMessage());
            }
        });
    loops.put(loopId, thread);
    activeLoopIdentifiers.put(loopKey, loopId);
    thread.start();
    return loopId;
  }

  public static void stopAllLoops() {
    Set<Integer> loopIds = new HashSet<>(loops.keySet());
    for (int loopId : loopIds) {
      stopLoop(loopId);
    }
    loops.clear();
    activeLoopIdentifiers.clear();
  }

  public static void listLoops() {
    System.out.println("Aktive Loops: " + loops.keySet());
  }

  /** Verbindung zum Pico schliessen */
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
}
