/**
 * Beschreiben Sie hier die Klasse Bedarfsampel.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Bedarfsampel{
  // Instanzvariablen - ersetzen Sie das folgende Beispiel mit Ihren Variablen

  private int s_rot = 16;
  private int s_gelb = 17;
  private int s_gruen = 18;
  private int f_rot = 20;
  private int f_gruen = 22;
  private int lID;


  /**
   * Konstruktor für Objekte der Klasse Bedarfsampel
   */
  public Bedarfsampel(){
    lID = -1;
  }

  public void start(){
    PicoIO.open();
    PicoIO.clearPressed();
    PicoIO.ledOn(s_gruen);
    PicoIO.ledOn(f_rot);
    lID = PicoIO.startLoop(this,"loop");   
  }

  private void loop(){
    if (PicoIO.wasPressed()){
      schaltePhase();
    }
  }
  
  public void stopp(){
    if (lID != -1){
      PicoIO.stopLoop(lID);
      lID = -1;
    }
    PicoIO.ledsOff();
  }
  
  public void beenden(){
    PicoIO.close();
  }
  private void schaltePhase() {

    //Phase 1: Warte, dann: Seite – Rot aus, Gelb an, Grün aus
    PicoIO.pause(500);
    PicoIO.ledOn(s_gelb);
    PicoIO.ledOff(s_gruen);

    // Phase 2: Warte, dann: Seite – Rot an, Gelb aus, Grün aus
    PicoIO.pause(1000);
    PicoIO.ledOn(s_rot);
    PicoIO.ledOff(s_gelb);

    // Phase 3: Warte, dann: Fußgänger – Rot aus, Grün an
    PicoIO.pause(500);
    PicoIO.ledOff(f_rot);
    PicoIO.ledOn(f_gruen);

    // Phase 4: Warte, dann: Fußgänger – Rot an, Grün aus
    PicoIO.pause(4000);
    PicoIO.ledOn(f_rot);
    PicoIO.ledOff(f_gruen);
    PicoIO.clearPressed();
    
    // Phase 5: Warte, dann: Seite – Rot und Gelb an, Grün aus
    PicoIO.pause(500);
    PicoIO.ledOn(s_gelb);

    // Phase 6: Warte, dann: Seite – Rot und Gelb aus, Grün an
    PicoIO.pause(500);
    PicoIO.ledOff(s_rot);
    PicoIO.ledOff(s_gelb);
    PicoIO.ledOn(s_gruen);

    // Abschließende Pause
    PicoIO.pause(2000);

  }

}
