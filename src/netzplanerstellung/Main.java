package netzplanerstellung;

import netzplanerstellung.datenhaltung.*;
import netzplanerstellung.logik.*;

import java.util.List;

public class Main {
    public static void main (String args []) {

	if (args.length != 2) {
	    System.err.println("Ungültige Anzahl an Argumenten! Erwarte Ein- und Ausgabedatei.");
	    System.exit(-1);
	}

	VorgangLeser leser = new VorgangLeser(args[0]);
	List<Vorgang> vorgaenge = leser.getVorgaenge();
	String ueberschrift = leser.getUeberschrift();

	for (Vorgang aktVorgang: vorgaenge) {
	    System.out.println(aktVorgang);
	}
    }
}
