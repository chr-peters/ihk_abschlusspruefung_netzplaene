package netzplanerstellung;

import netzplanerstellung.datenhaltung.*;
import netzplanerstellung.logik.*;

import java.util.List;

//TODO remove
import java.util.Arrays;

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

	    // erzeuge den Projekt Report
	    ProjektReport report = new ProjektReport(args[1]);
	    report.erzeugeReport(netzplan, ueberschrift);

	} catch (DateiFormatException e) {
	    // ein Fehler beim Einlesen ist aufgetreten!
	    // gebe die Informationen aus und beende das Programm mit einem Fehlercode
	    System.err.println(e.getMessage());
	    System.exit(-1);
	} catch (FileNotFoundException e) {
	    System.err.println(e.getMessage());
	    System.exit(-1);
	} catch (IOException e) {
	    System.err.println(e.getMessage());
	    System.exit(-1);
	} catch (NetzplanException e) {
	    // Fehler bei der Erstellung des Netzplanes
	    // gebe die Information aus und beende das Programm mit einem Fehlercode
	    System.err.println(e.getMessage());
	    System.exit(-1);
	}
    }
}
