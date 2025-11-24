/**
 * Static-Singleton-Hardwareklasse für PicoIO,
 * analog zum Crossroad-Projekt.
 *
 * Diese Klasse bietet eine statische API:
 *      hardware.ledOn(16);
 *      hardware.getLight();
 *      hardware.loop(this, "tick");
 *
 * Die Verbindung zum Pico wird automatisch beim ersten Zugriff aufgebaut.
 */

public final class hardware {

  // Singleton-Instanz des PicoIO
  private static PicoIO pico = null;

  // Sicherstellen, dass ein PicoIO existiert
  private static synchronized PicoIO getPico() {
    if (pico == null) {
      pico = new PicoIO();      // Konstruktor bleibt leer
    }
    return pico;
  }

  // -------------------------
  // LED-Funktionen
  // -------------------------

  public static void ledOn(int pin) {
    getPico().ledOn(pin);
  }

  public static void ledOff(int pin) {
    getPico().ledOff(pin);
  }

  public static void ledSet(int pin, boolean state) {
    getPico().ledSet(pin, state);
  }

  public static void ledSwitch(int pin) {
    getPico().ledSwitch(pin);
  }

  public static void ledsOff() {
    getPico().ledsOff();
  }

  // -------------------------
  // Sensorfunktionen
  // -------------------------

  public static int getLight() {
    return getPico().getLight();
  }

  public static double getLightNormalized() {
    return getPico().getLightNormalized();
  }

  public static int getHall() {
    return getPico().getHall();
  }

  public static boolean isContacted() {
    return getPico().isContacted();
  }

  public static boolean isPressed() {
    return getPico().isPressed();
  }

  public static boolean wasPressed() {
    return getPico().wasPressed();
  }

  // -------------------------
  // Buzzer
  // -------------------------

  public static void playBeep(int duration) {
    getPico().playBeep(duration);
  }

  public static void stopBeep() {
    getPico().stopBeep();
  }

  // -------------------------
  // Looping-Funktionen
  // -------------------------

  public static int loop(Object o, String m) {
    return getPico().startLoop(o, m);
  }

  public static int loopWithDelay(Object o, String m, int d) {
    return getPico().startLoopWithDelay(o, m, d);
  }

  public static void stopLoop(int id) {
    getPico().stopLoop(id);
  }

  public static void stopAllLoops() {
    getPico().stopAllLoops();
  }

  public static void listLoops() {
    getPico().listLoops();
  }

  // -------------------------
  // Timing
  // -------------------------

  public static void pause(int ms) {
    getPico().pause(ms);
  }

  public static void startClock() {
    getPico().startClock();
  }

  public static long stopClock() {
    return getPico().stopClock();
  }

  // -------------------------
  // Verbindung / Status
  // -------------------------

  public static boolean getStatus() {
    return getPico().getStatus();
  }

  public static void reconnect() {
    // erzwingt neue Instanz
    pico = new PicoIO();
  }

  public static void close() {
    getPico().close();
  }

}
