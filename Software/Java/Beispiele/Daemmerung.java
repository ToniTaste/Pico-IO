/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 10.02.2025
 * @author 
 */

public class Daemmerung {
  
  public static void main(String[] args) {
  
    PicoIO pico = new PicoIO();
    pico.open();
    int normwert = 500;
    
    while (!pico.wasPressed()) { 
      
      int messwert = pico.getLight();
      int differenz = messwert - normwert;

      if (differenz < -200) {
        pico.ledOn(19);
      } else if (differenz > 200){
        pico.ledOff(19);
      }
      else{
        pico.ledDim(19,(int) 255-(differenz+200)*255/400);
      }
    }
    pico.close();
  }

}
