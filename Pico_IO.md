# Überblick

Pico-IO ist eine Hardwarekomponente mit Sensoren und Aktoren. Sie
besteht aus einem Raspberry Pico an dem sieben LED -- beispielsweise zur
Anzeige von Ampeln oder eines Würfelergebnisses -- und ein
Minilautsprecher (Buzzer) zur Ausgabe von Tönen angeschlossen sind. Drei
Sensoren führen zu den analogen Eingängen des Raspberry Pico und dienen
der Erfassung von Helligkeit, Tastendruck und Magnetfeldstärke.

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image001.jpg){width="3.95294072615923in"
height="2.498885608048994in"}

Pico-IO lässt sich in den Entwicklungsumgebungen für den Raspberry Pico
H block- und textbasiert programmieren, beispielsweise mit
Microblocks.fun oder Thonny.

Pico-IO wurde für die 16. Landestagung der GI-Fachgruppe „Informatische
Bildung in Mecklenburg-Vorpommern" 2025 entwickelt und allen
Fachgruppenmitgliedern kostenfrei zur Verfügung gestellt.

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image002.jpg){width="2.96409230096238in"
height="3.2095166229221346in"}

Pico-IO unterliegt der Creative Commons Licence CC BY-SA 4. Alle
Materialien, Schaltpläne und Bilder und Programmbeispiele stehen somit
unter einer freien Lizenz und auf der Webseite der Fachgruppe
„Informatische Bildung in MV" jedem zur Verfügung.

**Bei der Version für die Landestagung wurden alle Bauelemente
aufgelötet, jedoch nicht alle Lötpunkte gesetzt. Das Löten des
Mikrotasters und einer Steckleiste ist zu beenden.**

# Hardware

## Schaltplan

Das zentrale Element der Hardwarekomponente ist der steckbare Raspberry
Pi Pico H. Von diesem werden die Pins 20 bis 36 benutzt. Es können die
Version 1 und 2 des Pico (W)H verwendet werden.

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image003.png){width="2.458215223097113in"
height="2.4015748031496065in"}

Pico-IO verfügt über drei Sensoren:

- GPIO 26: Tastsensor Wertebereich: \[\< 10, 1023\]

- GPIO 27: Magnetsensor Wertebereich: \[≈ 275 ... ≈ 825\], Normalwert:
  ≈ 555

- GPIO 28: Lichtsensor Wertebereich: \[\< 10 ... 1023\]

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image004.png){width="3.7501312335958006in"
height="1.8503937007874016in"}

Pico-IO besitzt acht Aktoren:

- GPIO 16 ... 22: LED-Matrix

- GPIO 15: Tonausgabe über Buzzer

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image005.png){width="5.937588582677165in"
height="2.204724409448819in"}

## Bauelemente

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image006.jpg){width="3.748543307086614in" height="3.0in"}

  ------------------------------------------------------------------------------------------------------------------

  Bauelement        Bezeichnung   Wert                                              Symbolbild

  ----------------- ------------- --------------------------- ------------------------------------------------------

  LED               D1 ... D7     2x rot, 2x gelb, 2x grün,          ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image007.png){width="0.21875in"
                                  1x blau                                 height="0.4583333333333333in"}

  Mikrotaster       SW1           Kurzhubtaster 6x6                   ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image008.png){width="0.25in"
                                                                          height="0.3333333333333333in"}

  Fototransistor    T1            INL-5APT30                         ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image009.png){width="0.21875in"
                                                                          height="0.4583333333333333in"}

  Hallsensor        IC1           AH49FZ3-G1                         ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image010.png){width="0.28125in"
                                                                          height="0.3958333333333333in"}

  Widerstandsnetz   RN1           SIL 7-6 200 Ω               ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image011.png){width="0.52in" height="0.28in"}

  Widerstand        R1, R2        10 kΩ: braun -- schwarz --   ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image012.png){width="0.4270833333333333in"
                                  orange                                       height="9.375e-2in"}

  Widerstand        R3            2,2 kΩ: rot -- rot -- rot    ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image013.png){width="0.4270833333333333in"
                                                                               height="9.375e-2in"}

  Widerstand        R4            100 Ω: braun -- schwarz --   ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image014.png){width="0.4270833333333333in"
                                  braun                                        height="9.375e-2in"}

  Widerstand        R5            1 kΩ: braun -- schwarz --    ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image015.png){width="0.4270833333333333in"
                                  rot                                          height="9.375e-2in"}

  Transistor        T2            BC 547C                      ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image016.png){width="0.2916666666666667in"
                                                                          height="0.4479166666666667in"}

  Buzzer            BZ1           CEM 1203                     ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image017.png){width="0.2613363954505687in"
                                                                          height="0.2885586176727909in"}

  Steckleiste                     1x20-polig                   ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image018.png){width="1.3385826771653544in"
                                                                         height="6.971784776902887e-2in"}

  Raspberry Pi Pico               Raspberry Pi Pico (W)H 1     ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image019.png){width="0.31449365704286963in"
                                  oder 2                                  height="0.7961876640419947in"}

  ------------------------------------------------------------------------------------------------------------------

Für Nachbauten sollten die Widerstandswerte für die LED-Matrix in
Abhängigkeit von der Helligkeit und von der zulässigen Strombelastung
des GPIO-Ausgangs angepasst werden. Der Hallsensor sollte gegen ein
Modell getauscht werden, dass auch bei schwachen Magnetfeldern
unterscheidbare Messwerte liefert.

Die Kosten für die Bauelemente liegen bei ca. 7 bis 14 EUR in
Abhängigkeit vom Mengenrabatt.

## Platine

  ----------------------------------------------------- ----------------------------------------------------

                    Bestückungsseite                                          Lötseite

   ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image020.jpg){width="2.2139074803149605in"   ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image021.jpg){width="2.213943569553806in"
             height="2.1653543307086616in"}                        height="2.1653543307086616in"}

  ----------------------------------------------------- ----------------------------------------------------

Im Falle einer Überarbeitung sollte die Anordnung der Anschlüsse von IC1
so geändert werden, dass Pin 2 am kürzesten ist.

Die Kosten für die Platine hängt stark vom Anbieter und der Menge ab.
Die 100 Platinen für die Landestagung kosteten seinerzeit incl. Versand,
Zoll und Steuern etwas über 50 EUR.

## Aufbau

### Werkzeug und Zubehör

- Lötkolben für elektronische Bauelemente

- Seitenscheider

- Flachzange

- Lötzinn 1 mm Durchmesser SN99 mit (möglichst wasserlöslichem)
  Flussmittel

Der Aufbau erfolgt vom flachsten zum höchsten Bauelement. Diese werden
auf der Bestückungsseite platziert und auf der Lötseite gelötet. Bei
bestimmten Bauelemente ist die Polung zu beachten.

### Hall-Sensor

IC1 ist für die Erfassung der Magnetfeldstärke zuständig. Es handelt
sich um einen integrierten Schaltkreis, der keine Zusatzbeschaltung
benötigt. Seine Anschlüsse müssen so gebogen sein, dass er liegend
eingelötet werden kann. Die bedruckte schmale Fläche zeigt nach oben.
Zum Biegen sollte eine Fachzange verwendet werden.

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image010.png){width="0.28125in" height="0.3958333333333333in"}

### Widerstände

R3 bis R5 begrenzen den Stromfluss auf zulässige Werte. R1 und R2 sorgen
an den Eingängen des Raspberry Pico als sog. Pull-Down-Widerstände für
definierte Eingangssignale. R1 bis R5 sind entsprechen ihrer
Werte/Codierung zu platziert.

R1: ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image012.png){width="0.4270833333333333in"
height="9.375e-2in"}, R2:
![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image013.png){width="0.4270833333333333in"
height="9.375e-2in"}, R3:
![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image014.png){width="0.4270833333333333in"
height="9.375e-2in"}, R4:
![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image015.png){width="0.4270833333333333in"
height="9.375e-2in"}, R5:
![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image015.png){width="0.4270833333333333in" height="9.375e-2in"}

### Widerstandsnetz

RN1 ist ein Widerstandsnetz und fasst mehrere Widerstände sternförmig
zusammen. So wird der Platzbedarf auf der Platine reduziert. Auf der
Beschriftungsseite des Bauelements markiert ein Punkt Pin 1 und somit
die Sternmitte. Dieser Pin gehört in das quadratisch umrandete Loch auf
der Platine.

Bauelement: ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image011.png){width="0.52in" height="0.28in"}
interner Aufbau: ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image022.png){width="0.5519991251093613in"
height="0.3324201662292213in"}

### Taster

Der Taster SW1 dient als Eingabesensor.

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image023.png){width="0.22133858267716536in"
height="0.2951181102362205in"}

### Transistor

Der Transistor T2 schaltet das Signal zum Buzzer und verhindert eine
Überlastung des Prozessors. Beim Einbau ist auf die korrekte Polung zu
achten. Die runde Seite des Gehäuses zeigt zu R4 und R5.

Symbolbild: ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image024.png){width="0.2559492563429571in"
height="0.39306430446194224in"} Bauelement im Schaltplan:
![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image025.png){width="0.36808836395450567in"
height="0.4222222222222222in"}

### Buzzer

Der Buzzer BZ1 erzeugt bei Verwendung eines schwingenden
Spannungssignals eine Tonausgabe. Auf dem Buzzergehäuse befindet sich
ein Plus-Symbol. Der zugehörige PIN ist in den auf der Platine mit Plus
gekennzeichneten Anschluss zu stecken.

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image026.png){width="0.36645122484689413in"
height="0.4046237970253718in"}

### LED

Die Leuchtdioden (LED) dienen der Ausgabe. Um Ampeln programmieren zu
können, ist als Farbfolge von D1 zu D3 und von D5 zu D7 jeweils rot --
gelb -- grün zu wählen. Die blaue LED ist D4. Beim Einbau ist die Polung
zu beachten. Der Platinenaufdruck zeigt die abgeflachte Seite der LED
an.

Symbolbild: ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image027.png){width="0.21875in"
height="0.4583333333333333in"} Bauelement im Schaltplan:
![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image028.png){width="0.380333552055993in"
height="0.5976662292213474in"}

### Fototransistor

Der Fototransistor T1 sieht wie eine LED im klaren Gehäuse aus. Es
handelt sich jedoch um einen Sensor, der den Strom in Abhängigkeit von
der Helligkeit fließen lässt. Für den Einbau muss er korrekt gepolt wie
im Aufdruck abgebildet eingelötet werden. Die abgeflachte Seite zeigt
nach rechts.

Symbolbild: ![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image029.png){width="0.21875in"
height="0.4583333333333333in"} Bauelement im Schaltplan:
![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image030.png){width="0.303999343832021in"
height="1.0778160542432196in"}

### Steckerleisten

Die beiden Steckerleisten tragen den Raspberry Pi Pico H. Nach Abschluss
der Lötarbeiten kann der Pico eingesetzt werden. Dabei ist auf die
korrekte Ausrichtung zu achten. Der USB-Port befindet sich rechts über
den Widerständen R4 und R5.

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image031.png){width="2.0in" height="0.10416666666666667in"}

### Raspberry Pi Pico (W)H

Der Raspberry Pi Pico ist ein kompakter und kostengünstiger
Mikrocontroller. Die Anschlüsse GP0 bis GP28 können sehr flexibel als
Ein- oder Ausgänge programmiert und genutzt werden.

Eine besondere Rolle hat die Taste BOOTSEL. Sie ermöglicht es, den
Raspberry Pi Pico in den Bootloader-Modus zu versetzen. Beim Drücken der
BOOTSEL-Taste während des Anschlusses an einen Computer erscheint der
Pico als USB-Massenspeicher. So kann Firmware oder lauffähige Systeme,
beispielsweise MicroPython oder das Arduino-Demo-Programme, einfach auf
den Mikrocontroller übertragen werden.

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image032.png){width="6.299305555555556in"
height="4.289050743657043in"}

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

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image033.png){width="6.299305555555556in"
height="2.286291557305337in"}

## Blockbasierte Programmierung mit Mind+

Mind+ kann blockbasiert in der Offline-Version ab 1.8.1 auf Pico-IO
zugreifen.

Pico-IO benötigt eine Firmware. Diese ist vor der ersten Verwendung aus
Mind+ heraus über das Menü „Verbinden" zu installieren. Aktuell
funktionieren einige Hardwarezugriffe nicht zuverlässig. Dies soll in
den nächsten Versionen jedoch behoben werden.

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image034.png){width="6.299305555555556in"
height="2.727426727909011in"}

## Textbasierte Programmierung mit MicroPython in Thonny

Thonny ist ein Beispiel für eine textbasierte Entwicklungsumgebung.

Pico-IO benötigt eine Firmware (MicroPython). Diese ist vor der ersten
Verwendung aus Thonny heraus im Menü „Werkzeuge" 🡪 „Interpreter" 🡪
„MicroPython installieren oder aktualisieren" zu installieren. Unter
„variant" muss der Eintrag „Raspberry Pi Pico" gewählt wählen. Nach
erfolgreicher Installation muss im Interpreterfenster aus MicroPython
gewechselt werden.

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image035.png){width="5.480418853893263in"
height="3.394575678040245in"}

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

![](D:\SynologyDrive\Informatik\Allgemeines\Projekte\KiCad\Pico-IO\Abb\image036.png){width="6.299305555555556in"
height="2.772726377952756in"}

## Anwendungsbeispiele

In den Unterlagen zum Projekt finden sich vier Anwendungsbeispiele, die
in Microblock, MicroPython und Mind+ programmiert wurde. Das
Demo-Programm, welches auch das Schummelwürfelspiel umfasst, wurde mit
der Arduino-IDE entwickelt.

(1) **Fensteralarm (Aktor: Buzzer; Sensor: Magnetstärke)\
    **Ein Alarmsignal wird ausgelöst, sobald der Fenstermagnet vom
    Fenstersensor entfernt wird.

(2) **Dämmerungslichtautomat (Aktor: LED, Sensor: Fototransistor)\
    **Fällt die Helligkeit unter einen bestimmten Grenzwert, so wird die
    Straßenbeleuchtung aktiviert. Während der Dämmerung ist das Licht
    der Lampe gedimmt.

(3) **Bedarfsampel (Aktor: LED; Sensor: Taster)\
    **Ein ampelgeschalteter Fußgängerüberweg wird gesteuert. Die
    Straßenampel wechselt systematisch auf Rot, wenn der Fußgänger am
    Knopf seinen Bedarf gemeldet hat. Die zugehörige Fußgängerampel
    schaltet auf grün. Nach einer Überquerungszeit wechseln die Ampeln
    in den Ausgangszustand zurück.

(4) **Schummelwürfel (Aktor: LED, Buzzer; Sensor: Taster, Magnetstärke,
    Fototransistor)\
    **Falls es nicht zu hell ist, sind Glücksspiele erlaubt. Ein Druck
    auf den Knopf wirft einen virtuellen Würfel und zeigt das Ergebnis
    auf dem LED-Feld an. Wird ein Magnet an den Magnetsensor gelegt, ist
    das Würfelergebnis immer eine sechs.
