/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 10.02.2025
 * @author 
 */

public class Daemmerung {
  
  public static void main(String[] args) {
  
    PicoIO.open();
    int normwert = 500;
    
    while (!PicoIO.wasPressed()) { 
      
      int messwert = PicoIO.getLight();

      int differenz = messwert - normwert;

      if (differenz < -200) {
        PicoIO.ledOn(19);
      } else if (differenz > 200){
        PicoIO.ledOff(19);
      }
      else{
        PicoIO.ledDim(19,(int) 255-(differenz+200)*255/400);
      }
      PicoIO.pause(100);
    }
    PicoIO.close();
  }

}
