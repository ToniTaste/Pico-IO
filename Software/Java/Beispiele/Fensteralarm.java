/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 10.02.2025
 * @author 
 */

public class Fensteralarm {
  
  public static void main(String[] args) {
    
    PicoIO.open();
    int normwert = 533;
    
    
    while (!PicoIO.wasPressed()) { 
      int messwert = PicoIO.getHall();
      if (Math.abs(messwert - normwert) < 25){
        PicoIO.playBeep(100);
        PicoIO.pause(500);
      }
    }
    
    PicoIO.close();
  }
  
  
}
