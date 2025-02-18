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
  private int loopID;
  private PicoIO pico;

  /**
   * Konstruktor für Objekte der Klasse Bedarfsampel
   */
  public Bedarfsampel(){
    pico = new PicoIO();
    loopID = -1;
  }

  public void start(){
    if (loopID == -1){
      pico.open();
      pico.clearPressed();
      pico.ledOn(s_gruen);
      pico.ledOn(f_rot);
      loopID = pico.startLoop(this,"loop");   
    }  
    else{
      System.out.println("Loop bereits gestartet");
    }

  }

  private void loop(){
    if (pico.wasPressed()){
      schaltePhase();
    }
  }

  public void stopp(){
    if (loopID != -1){
      pico.stopLoop(loopID);
      loopID = -1;
      pico.ledsOff();
      pico.close();
    }

  }

  private void schaltePhase() {
    //Phase 1: Warte, dann: Seite – Rot aus, Gelb an, Grün aus
    pico.pause(500);
    pico.ledOn(s_gelb);
    pico.ledOff(s_gruen);

    // Phase 2: Warte, dann: Seite – Rot an, Gelb aus, Grün aus
    pico.pause(1000);
    pico.ledOn(s_rot);
    pico.ledOff(s_gelb);

    // Phase 3: Warte, dann: Fußgänger – Rot aus, Grün an
    pico.pause(500);
    pico.ledOff(f_rot);
    pico.ledOn(f_gruen);

    // Phase 4: Warte, dann: Fußgänger – Rot an, Grün aus
    pico.pause(4000);
    pico.ledOn(f_rot);
    pico.ledOff(f_gruen);
    pico.clearPressed();

    // Phase 5: Warte, dann: Seite – Rot und Gelb an, Grün aus
    pico.pause(500);
    pico.ledOn(s_gelb);

    // Phase 6: Warte, dann: Seite – Rot und Gelb aus, Grün an
    pico.pause(500);
    pico.ledOff(s_rot);
    pico.ledOff(s_gelb);
    pico.ledOn(s_gruen);

    // Abschließende Pause
    pico.pause(2000);

  }

}
