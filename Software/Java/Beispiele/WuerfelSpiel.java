import java.util.Random;

public class WuerfelSpiel {
  private int THRESHOLD_LIGHT = 800;
  private int HALL_STANDARD = 533;
  private int loopID;
  private PicoIO pico;

  public WuerfelSpiel() {
    pico = new PicoIO();
    loopID = -1;
  }

  private void displayDice(int number) {
    int[][] ledPatterns = {
        {0, 0, 0, 0, 0, 0, 0},  // 0
        {0, 0, 0, 1, 0, 0, 0},  // 1
        {0, 0, 1, 0, 1, 0, 0},  // 2
        {1, 0, 0, 1, 0, 0, 1},  // 3
        {1, 0, 1, 0, 1, 0, 1},  // 4
        {1, 0, 1, 1, 1, 0, 1},  // 5
        {1, 1, 1, 0, 1, 1, 1}   // 6
      };

    int[] pattern = ledPatterns[number];
    for (int i = 0; i < pattern.length; i++) {
      pico.ledSet(16 + i, pattern[i] == 1);
    }
  }

  private void loop(){
    int lightValue = pico.getLight();
    if (lightValue > THRESHOLD_LIGHT) {
      pico.playBeep(200);
      pico.pause(500);
    } else if (pico.isPressed()) {
      int hallValue = pico.getHall();
      if (hallValue < HALL_STANDARD * 0.9 || hallValue > HALL_STANDARD * 1.1) {
        displayDice(6);
      } else {
        int number = (int) (6 * Math.random() + 1);
        displayDice(number);
      }
      pico.pause(1000);
      displayDice(0);
    }
  }

  public void starten(){
    if (loopID == -1){
      pico.open();
      loopID = pico.startLoop(this,"loop");
    }  
    else{
      System.out.println("Loop bereits gestartet)");
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

}
