# Pico-IO

Pico-IO ist eine Hardwarekomponente mit Sensoren und Aktoren. Sie
besteht aus einem Raspberry Pico, an dem sieben LED - beispielsweise zur
Anzeige von Ampeln oder eines Würfelergebnisses - und ein
Minilautsprecher (Buzzer) zur Ausgabe von Tönen angeschlossen sind. Drei
Sensoren führen zu den analogen Eingängen des Raspberry Pico und dienen
der Erfassung von Helligkeit, Tastendruck und Magnetfeldstärke.

| ![Pico-IO](Abb/image001.jpg) |
|:----------------------------:|

Pico-IO lässt sich in Entwicklungsumgebungen für den Raspberry Pico block- und textbasiert programmieren. Das System wurde erfolgreich mit Microblocks, Mind+, Thonny (MicroPython), Arduino (C++) und Java getestet. Vier Beispiele liegen in den Sprachen vor.

Pico-IO wurde für die 16. Landestagung der GI-Fachgruppe „Informatische
Bildung in Mecklenburg-Vorpommern" 2025 entwickelt und allen
Fachgruppenmitgliedern kostenfrei zur Verfügung gestellt.

| ![100 Pico-IO für Landestagung](Abb/image002.jpg) |
|:-------------------------------------------------:|

Pico-IO unterliegt der Creative Commons Licence CC BY-SA 4. Alle
Materialien, Schaltpläne und Bilder und Programmbeispiele stehen somit
unter einer freien Lizenz und auf der Webseite der Fachgruppe
„Informatische Bildung in MV" jedem zur Verfügung.

**Bei der Version für die Landestagung wurden alle Bauelemente
aufgelötet, jedoch nicht alle Lötpunkte gesetzt. Das Löten des
Mikrotasters und einer Steckleiste ist zu beenden.**

# Hardware

## Schaltplan

Die Schaltung wurde auf einem Breadboard aufgebaut, getestet und in KiCad übertragen. Die KiCad-Dateien liegen im Ordner Hardware. Den Schaltplan gibt es auch als [PDF-Dokument](Hardware/Schaltplan.pdf).

Das zentrale Element der Hardwarekomponente ist der steckbare Raspberry
Pi Pico H. Von diesem werden die Pins 20 bis 36 benutzt. Es können die
Version 1 und 2 des Pico (W)H verwendet werden.

| ![Schaltung Raspberry Pi Pico](Abb/image003.png) |
|:------------------------------------------------:|

Pico-IO verfügt über drei Sensoren:

- GPIO 26: Tastsensor Wertebereich: \[\< 10, 1023\]
- GPIO 27: Magnetsensor Wertebereich: \[≈ 275 ... ≈ 825\], Normalwert: ≈ 555
- GPIO 28: Lichtsensor Wertebereich: \[\< 10 ... 1023\]

| ![Schaltung Sensoren](Abb/image004.png) |
|:---------------------------------------:|

Pico-IO besitzt acht Aktoren:

- GPIO 16 ... 22: LED-Matrix
- GPIO 15: Tonausgabe über Buzzer

| ![Schaltung Aktoren](Abb/image005.png) |
|:--------------------------------------:|

## Bauelemente

| ![Bauelementübersicht](Abb/image006.jpg) |
|:----------------------------------------:|

| Bauelement         | Name      | Wert                                |  Symbolbild           |
|--------------------|-----------|-------------------------------------|-----------------------|
|  LED               | D1 ... D7 |  2x rot, 2x gelb, 2x grün, 1x blau  | ![](Abb/image007.png) |
|  Mikrotaster       | SW1       |  Kurzhubtaster 6x6                  | ![](Abb/image008.png) |
|  Fototransistor    | T1        |  INL-5APT30                         | ![](Abb/image009.png) |
|  Hallsensor        | IC1       |  AH49FZ3-G1                         | ![](Abb/image010.png) |
|  Widerstandsnetz   | RN1       |  SIL 7-6 200 Ω                      | ![](Abb/image011.png) |
|  Widerstand        | R1, R2    |  10 kΩ: braun - schwarz - orange    | ![](Abb/image012.png) |
|  Widerstand        | R3        |  2,2 kΩ: rot - rot - rot            | ![](Abb/image013.png) |
|  Widerstand        | R4        |  100 Ω: braun - schwarz - braun     | ![](Abb/image014.png) |
|  Widerstand        | R5        |  1 kΩ: braun - schwarz - rot        | ![](Abb/image015.png) |
|  Transistor        | T2        |  BC 547C                            | ![](Abb/image016.png) |
|  Buzzer            | BZ1       |  CEM 1203                           | ![](Abb/image017.png) |
|  Steckleiste       |           |  1x20-polig                         | ![](Abb/image018.png) |
|  Raspberry Pi Pico |           |  Raspberry Pi Pico (W)H 1           | ![](Abb/image019.png) |


Für Nachbauten sollten die Widerstandswerte für die LED-Matrix in Abhängigkeit von der Helligkeit und von der zulässigen Strombelastung
des GPIO-Ausgangs angepasst werden. Der Hallsensor sollte gegen ein Modell getauscht werden, das auch bei schwachen Magnetfeldern
unterscheidbare Messwerte liefert.

Die Kosten für die Bauelemente liegen bei ca. 7 bis 14 EUR in Abhängigkeit vom Mengenrabatt. 

## Platine

| Bestückungsseite                      | Lötseite                      |
|:-------------------------------------:|:-----------------------------:|
| ![Bestückungsseite](Abb/image020.jpg) | ![Lötseite](Abb/image021.jpg) |

Die Platinen wurden in KiCad entwickelt. Im Hardware-Ordner liegen alle Daten vor. Im Falle einer Überarbeitung sollte die Anordnung der Anschlüsse von IC1 so geändert werden, dass Pin 2 am kürzesten ist.

Die Kosten für die Platine hängen stark vom Anbieter und der Menge ab.
Die 100 Platinen für die Landestagung kosteten seinerzeit incl. Versand,
Zoll und Steuern etwas über 50 EUR.

## Aufbau

### Werkzeug und Zubehör

- Lötkolben für elektronische Bauelemente
- Seitenscheider
- Flachzange
- Lötzinn 1 mm Durchmesser SN99 mit (möglichst wasserlöslichem) Flussmittel

Der Aufbau erfolgt vom flachsten zum höchsten Bauelement. Diese werden
auf der Bestückungsseite platziert und auf der Lötseite gelötet. Bei
bestimmten Bauelemente ist die Polung zu beachten.

### Hall-Sensor 

| ![](Abb/image010.png) |
|:----------------------------------------:|

IC1 ist für die Erfassung der Magnetfeldstärke zuständig. Es handelt
sich um einen integrierten Schaltkreis, der keine Zusatzbeschaltung
benötigt. Seine Anschlüsse müssen so gebogen sein, dass er liegend
eingelötet werden kann. Die bedruckte schmale Fläche zeigt nach oben.
Zum Biegen sollte eine Flachzange verwendet werden.

### Widerstände

| R1: ![](Abb/image012.png), R2: ![](Abb/image012.png), R3: ![](Abb/image013.png), R4: ![](Abb/image014.png), R5: ![](Abb/image015.png) |
|:----------------------------------------:|

R3 bis R5 begrenzen den Stromfluss auf zulässige Werte. R1 und R2 sorgen
an den Eingängen des Raspberry Pico als sog. Pull-Down-Widerstände für
definierte Eingangssignale. R1 bis R5 sind entsprechend ihrer
Werte/Codierung zu platziert.

### Widerstandsnetz

| Bauelement: ![](Abb/image011.png) | interner Aufbau: ![](Abb/image022.png) |
|:---------------------------------:|:---------------------------------------|

RN1 ist ein Widerstandsnetz und fasst mehrere Widerstände sternförmig
zusammen. So wird der Platzbedarf auf der Platine reduziert. Auf der
Beschriftungsseite des Bauelements markiert ein Punkt Pin 1 und somit
die Sternmitte. Dieser Pin gehört in das quadratisch umrandete Loch auf
der Platine.

### Taster

| ![](Abb/image023.png) |
|:---------------------:|

Der Taster SW1 dient als Eingabesensor.

### Transistor

| Symbolbild: ![](Abb/image024.png) | Bauelement im Schaltplan: ![](Abb/image025.png) |
|:---------------------------------:|:-----------------------------------------------:|

Der Transistor T2 schaltet das Signal zum Buzzer und verhindert eine
Überlastung des Prozessors. Beim Einbau ist auf die korrekte Polung zu
achten. Die runde Seite des Gehäuses zeigt zu R4 und R5.

### Buzzer

| ![](Abb/image026.png) |
|:----------------------:|

Der Buzzer BZ1 erzeugt bei Verwendung eines schwingenden
Spannungssignals eine Tonausgabe. Auf dem Buzzergehäuse befindet sich
ein Plus-Symbol. Der zugehörige PIN ist in den auf der Platine mit Plus
gekennzeichneten Anschluss zu stecken.

### LED

| Symbolbild: ![](Abb/image027.png) | Bauelement im Schaltplan: ![](Abb/image028.png) |
|-----------------------------------|-------------------------------------------------|

Die Leuchtdioden (LED) dienen der Ausgabe. Um Ampeln programmieren zu
können, ist als Farbfolge von D1 zu D3 und von D5 zu D7 jeweils rot -
gelb - grün zu wählen. Die blaue LED ist D4. Beim Einbau ist die Polung
zu beachten. Der Platinenaufdruck zeigt die abgeflachte Seite der LED
an.


### Fototransistor

| Symbolbild: ![](Abb/image029.png) | Bauelement im Schaltplan: ![](Abb/image030.png) |
|:----------------------------------:|:----------------------------------------------:|

Der Fototransistor T1 sieht wie eine LED im klaren Gehäuse aus. Es
handelt sich jedoch um einen Sensor, der den Strom in Abhängigkeit von
der Helligkeit fließen lässt. Für den Einbau muss er korrekt gepolt wie
im Aufdruck abgebildet eingelötet werden. Die abgeflachte Seite zeigt
nach rechts.

### Steckerleisten

| ![](Abb/image031.png) |
|:---------------------:|

Die beiden Steckerleisten tragen den Raspberry Pi Pico H. Nach Abschluss
der Lötarbeiten kann der Pico eingesetzt werden. Dabei ist auf die
korrekte Ausrichtung zu achten. Der USB-Port befindet sich rechts über
den Widerständen R4 und R5.


### Raspberry Pi Pico (W)H

Der Raspberry Pi Pico ist ein kompakter und kostengünstiger
Mikrocontroller. Die Anschlüsse GP0 bis GP28 können sehr flexibel als
Ein- oder Ausgänge programmiert und genutzt werden.

Eine besondere Rolle hat die Taste BOOTSEL. Sie ermöglicht es, den
Raspberry Pi Pico in den Bootloader-Modus zu versetzen. Beim Drücken der
BOOTSEL-Taste während des Anschlusses an einen Computer erscheint der
Pico als USB-Massenspeicher. So kann Firmware oder lauffähige Systeme,
beispielsweise MicroPython oder das Arduino-Demo-Programm, einfach auf
den Mikrocontroller übertragen werden.

| ![](Abb/image032.png) |
|:---------------------:|

# Programmierung

## Treiber

Aktuelle Betriebssysteme erkennen den Pico-IO automatisch.

## Blockbasierte Programmierung mit Microblocks.fun

Microblocks.fun ist über die gleichnamige Website als Online- und
Offline-Editor verfügbar.

Pico-IO benötigt eine Firmware. Diese ist vor der ersten Verwendung aus
Microblocks zu installieren. Im ⚙️-Menü wählt man dazu zunächst
„aktualisiere Firmware auf dem Board" und dann „RP 2040". Anschließend
ist den Anweisungen zu folgen.

Über die Blockkategorie „Pins" lassen sich die Aktoren und Sensoren des
Pico-IO nutzen. Module ermöglichen die Einbindung von Erweiterungen
beispielsweise zur Ausgabe von Klängen. Besonders elegant gelöst ist die
grafische Darstellung von Messwerten im Diagramm.

![](Abb/image033.png)

## Blockbasierte Programmierung mit Mind+

Mind+ kann blockbasiert in der Offline-Version ab 1.8.1 auf Pico-IO
zugreifen.

Pico-IO benötigt eine Firmware. Diese ist vor der ersten Verwendung aus
Mind+ heraus über das Menü „Verbinden" zu installieren. Aktuell
funktionieren einige Hardwarezugriffe nicht zuverlässig. Dies soll in
den nächsten Versionen jedoch behoben werden.

![](Abb/image034.png)

## Textbasierte Programmierung mit MicroPython in Thonny

Thonny ist ein Beispiel für eine textbasierte Entwicklungsumgebung.

Pico-IO benötigt eine Firmware (MicroPython). Diese ist vor der ersten
Verwendung aus Thonny heraus im Menü „Werkzeuge" 🡪 „Interpreter" 🡪
„MicroPython installieren oder aktualisieren" zu installieren. Unter
„variant" muss der Eintrag „Raspberry Pi Pico" gewählt wählen. Nach
erfolgreicher Installation muss im Interpreterfenster aus MicroPython
gewechselt werden.

![](Abb/image035.png)

## Textbasierte Programmierung mit C++ in der Arduino-IDE

Die Arduino-IDE lässt sich als textbasiertes Entwicklungssystem nutzen
und erzeugt zudem ausführbare UF2-Dateien, die dann auf das System
geladen werden können.

Die zusätzliche Board-Verwalter-URL
„<https://github.com/earlephilhower/arduino-pico/releases/download/global/package_rp2040_index.json>"
muss in den Einstellungen eingetragen und dann im Boardverwalter das
Paket \"Raspberry Pi RP2040 Boards\" von Earle F. Philhower, III
installiert werden. Anschließend ist per Bootloadermodus das Board nebst
Port zu wählen.

![](Abb/image036.png)

## Textbasierte Programmierung mit Java mind. Version 17 in BlueJ oder JavaEditor (unter Windows)

Pico-IO benötigt eine [Firmware](Software/Java/Firmata), die mittels Arduino aus der Standard-Firmata generiert wurde. Diese ist vor der ersten
Verwendung auf dem Pico zu installieren. Während des Anschlusses an einen Computer muss die Taste BOOTSEL gedrückt werden. Der Pico erscheint dann als USB-Massenspeicher. Nun wird die PicoIO.uf2-Datei auf das Laufwerk kopiert. Nach einem Neustart des Pico blinkt die Onboard-LED mehrfach und das System ist bereit. 

Damit aus Java heraus auf Pico-IO zugegriffen werden kann, sind einige [Bibliotheken](Software/Java/Bibliotheken) in Java einzubinden. 

- In BlueJ geht dies im Menü  „Werkzeuge" 🡪 „Einstellungen" 🡪 „Bibliotheken" 🡪 „Benutzerbibliotheken". Alternativ kann man auch im jeweiligen BlueJ-Projektordner einen Unterordner namens "+libs" anlegen und die Bibliotheken dort hinein kopieren. BlueJ findet diese dann stets automatisch.
- Im JavaEditor erreicht man Einbindung über „Fenster" 🡪 „Konfiguration" 🡪 „Classpath User". Dort sind die Bibliotheken aus dem Ordner anzugeben.

Der Quelltext der Hauptbibliothek PicoIO.java befindet sich im Ordner [Source](Software/Java/Source)


## Anwendungsbeispiele

In den Unterlagen zum Projekt finden sich vier [Anwendungsbeispiele](Software/), die
in Microblock, MicroPython, Mind+, Arduino und Java programmiert wurde. Das
Demo-Programm, welches auch das Schummelwürfelspiel umfasst, wurde mit
der Arduino-IDE entwickelt, liegt aber auch in Python vor.

(1) **Fensteralarm (Aktor: Buzzer; Sensor: Magnetstärke)**\
    Ein Alarmsignal wird ausgelöst, sobald der Fenstermagnet vom
    Fenstersensor entfernt wird.

(2) **Dämmerungslichtautomat (Aktor: LED, Sensor: Fototransistor)**\
    Fällt die Helligkeit unter einen bestimmten Grenzwert, so wird die
    Straßenbeleuchtung aktiviert. Während der Dämmerung ist das Licht
    der Lampe gedimmt.

(3) **Bedarfsampel (Aktor: LED; Sensor: Taster)**\
    Ein ampelgeschalteter Fußgängerüberweg wird gesteuert. Die
    Straßenampel wechselt systematisch auf Rot, wenn der Fußgänger am
    Knopf seinen Bedarf gemeldet hat. Die zugehörige Fußgängerampel
    schaltet auf grün. Nach einer Überquerungszeit wechseln die Ampeln
    in den Ausgangszustand zurück.

(4) **Schummelwürfel (Aktor: LED, Buzzer; Sensor: Taster, Magnetstärke,
    Fototransistor)**\
    Falls es nicht zu hell ist, sind Glücksspiele erlaubt. Ein Druck
    auf den Taster wirft einen virtuellen Würfel und zeigt das Ergebnis
    auf dem LED-Feld an. Wird ein Magnet an den Magnetsensor gelegt, ist
    das Würfelergebnis immer eine sechs.
