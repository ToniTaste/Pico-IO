/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 10.02.2025
 * @author 
 */

public class Fensteralarm {
  
  public static void main(String[] args) {
    
    PicoIO pico = new PicoIO();
    pico.open();
    int normwert = 533;
    
    
    while (!pico.wasPressed()) { 
      int messwert = pico.getHall();
      if (Math.abs(messwert - normwert) < 25){
        pico.playBeep(100);
        pico.ledOn(19);
        pico.pause(500);
        pico.ledOff(19);
      }
    }
    
    pico.close();
  }
  
  
}
