package netzplanerstellung;

import netzplanerstellung.datenhaltung.*;
import netzplanerstellung.logik.*;

import java.util.List;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main (String args []) {

	if (args.length != 2) {
	    System.err.println("Ungültige Anzahl an Argumenten! Erwarte Ein- und Ausgabedatei.");
	    System.exit(-1);
	}

	try {
	    // lese die Vorgänge und die Überschrift ein
	    VorgangLeser leser = new VorgangLeser(args[0]);

	    List<Vorgang> vorgaenge = leser.getVorgaenge();
	    String ueberschrift = leser.getUeberschrift();

	    // erzeuge den Netzplan
	    Netzplan netzplan = new Netzplan(vorgaenge);

	    for (Vorgang aktVorgang: vorgaenge) {
		System.out.println(aktVorgang);
	    }
	} catch (DateiFormatException e) {
	    // ein Fehler beim Einlesen ist aufgetreten!
	    // gebe die Informationen aus und beende das Programm mit einem Fehlercode
	    System.err.println(e.getMessage());
	    System.exit(-1);
	} catch (FileNotFoundException e) {
	    System.err.println(e.getMessage());
	    System.exit(-1);
	} catch (IOException e) {
	    // dieser Fehler sollte in der Praxis nicht auftreten, daher gebe den gesamten
	    // Stack aus
	    System.err.println("Ein schwerwiegender Fehler ist aufgetreten. Bitte kontaktieren Sie sofort den Entwickler!");
	    e.printStackTrace();
	    System.exit(-1);
	} catch (NetzplanException e) {
	    // Fehler bei der Erstellung des Netzplanes
	    // gebe die Information aus und beende das Programm mit einem Fehlercode
	    System.err.println(e.getMessage());
	    System.exit(-1);
	}
    }
}
