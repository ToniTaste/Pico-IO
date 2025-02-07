// Pin-Definitionen
const int led_s_rot = 16;
const int led_s_gelb = 17;
const int led_s_gruen = 18;
const int led_f_rot = 20;
const int led_f_gruen = 22;
const int button = 26;

void schaltePhase() {
    // Phase 1: Warte, dann: Seite – Rot aus, Gelb an, Grün aus
    delay(500);
    digitalWrite(led_s_gelb, HIGH);
    digitalWrite(led_s_gruen, LOW);

    // Phase 2: Warte, dann: Seite – Rot an, Gelb aus, Grün aus
    delay(1000);
    digitalWrite(led_s_rot, HIGH);
    digitalWrite(led_s_gelb, LOW);

    // Phase 3: Warte, dann: Fußgänger – Rot aus, Grün an
    delay(500);
    digitalWrite(led_f_rot, LOW);
    digitalWrite(led_f_gruen, HIGH);

    // Phase 4: Warte, dann: Fußgänger – Rot an, Grün aus
    delay(4000);
    digitalWrite(led_f_rot, HIGH);
    digitalWrite(led_f_gruen, LOW);

    // Phase 5: Warte, dann: Seite – Rot und Gelb an, Grün aus
    delay(500);
    digitalWrite(led_s_gelb, HIGH);

    // Phase 6: Warte, dann: Seite – Rot und Gelb aus, Grün an
    delay(500);
    digitalWrite(led_s_rot, LOW);
    digitalWrite(led_s_gelb, LOW);
    digitalWrite(led_s_gruen, HIGH);

    // Abschließende Pause
    delay(2000);
}

void setup() {
    pinMode(led_s_rot, OUTPUT);
    pinMode(led_s_gelb, OUTPUT);
    pinMode(led_s_gruen, OUTPUT);
    pinMode(led_f_rot, OUTPUT);
    pinMode(led_f_gruen, OUTPUT);
    pinMode(button, INPUT_PULLDOWN);

    // Initiale Zustände
    digitalWrite(led_s_rot, LOW);
    digitalWrite(led_s_gelb, LOW);
    digitalWrite(led_s_gruen, HIGH);
    digitalWrite(led_f_rot, HIGH);
    digitalWrite(led_f_gruen, LOW);
}

void loop() {
    if (digitalRead(button) == HIGH) {
        schaltePhase();
    }
}
