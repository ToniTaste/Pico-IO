import java.util.Random;

public class WuerfelSpiel {
  private int THRESHOLD_LIGHT = 800;
  private int HALL_STANDARD = 533;
  private int loopID;

  public WuerfelSpiel() {
    PicoIO.open();
    loopID = -1;
  }

  private static void displayDice(int number) {
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
      PicoIO.ledSet(16 + i, pattern[i] == 1);
    }
  }

  private void loop(){
    int lightValue = PicoIO.getLight();
    if (lightValue > THRESHOLD_LIGHT) {
      PicoIO.playBeep(200);
      PicoIO.pause(500);
    } else if (PicoIO.isPressed()) {
      int hallValue = PicoIO.getHall();
      if (hallValue < HALL_STANDARD * 0.9 || hallValue > HALL_STANDARD * 1.1) {
        displayDice(6);
      } else {
        int number = (int) (6 * Math.random() + 1);
        displayDice(number);
      }
      PicoIO.pause(1000);
      displayDice(0);
    }
  }

  public void starten(){
    loopID = PicoIO.startLoop(this,"loop");  

  }

  public void stopp(){
    if (loopID != -1){
      PicoIO.stopLoop(loopID);
      loopID = -1;
    }
    PicoIO.ledsOff();
  }

  public void beenden(){
    PicoIO.close();
  }
}
