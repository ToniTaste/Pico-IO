// GPIO Definitionen
#define BUZZER 15
#define BUTTON 26
#define HALL_SENSOR 27
#define LIGHT_SENSOR 28

int leds[] = { 16, 17, 18, 19, 20, 21, 22 };

// Konstanten
const int THRESHOLD_LIGHT = 800;  // Helligkeitsschwelle
const int HALL_STANDARD = 533;    // Hall-Regelwert

void setup() {
  pinMode(BUZZER, OUTPUT);
  pinMode(BUTTON, INPUT_PULLDOWN);
  pinMode(HALL_SENSOR, INPUT);
  for (int i = 0; i < 7; i++) {
    pinMode(leds[i], OUTPUT);
  }
  
  //Zufallsgenerator mit Helligkeitssensor zufällig initialisieren
  randomSeed(analogRead(LIGHT_SENSOR));
}

void playBuzzer(int frequency, int duration) {
  tone(BUZZER, frequency, duration);
  delay(duration);
  noTone(BUZZER);
}


void displayDice(int number) {
  int patterns[7][7] = {
    { 0, 0, 0, 0, 0, 0, 0 },  // 0
    { 0, 0, 0, 1, 0, 0, 0 },  // 1
    { 0, 0, 1, 0, 1, 0, 0 },  // 2
    { 1, 0, 0, 1, 0, 0, 1 },  // 3
    { 1, 0, 1, 0, 1, 0, 1 },  // 4
    { 1, 0, 1, 1, 1, 0, 1 },  // 5
    { 1, 1, 1, 0, 1, 1, 1 }   // 6
  };

  for (int i = 0; i < 7; i++) {
    digitalWrite(leds[i], patterns[number][i]);
  }
}

void loop() {


  // Licht-Sensor-Warnsignal
  int lightValue = analogRead(LIGHT_SENSOR);
  if (lightValue > THRESHOLD_LIGHT) {
    playBuzzer(500, 200);
    delay(500);
  }
  else if (digitalRead(BUTTON) == HIGH) { //Tastendruck für Würfel
    // Schummeln mit Magnetsensor
    int hallValue = analogRead(HALL_SENSOR);
    if (hallValue < HALL_STANDARD * 0.9 || hallValue > HALL_STANDARD * 1.1) {
      displayDice(6);
    } else { //ohne Schummeln
      int number = random(1, 7);
      displayDice(number);
    }
    delay(1000);
    displayDice(0);
  }
}
