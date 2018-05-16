package netzplanerstellung.datenhaltung;

import netzplanerstellung.logik.*;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * Klasse zur Erzeugung der Ausgabedatei.
 */
public class ProjektReport {
    private String datei;

    public ProjektReport(String datei) {
	this.datei = datei;
    }

    public void erzeugeReport(Netzplan plan, String ueberschrift) throws IOException{
	// versuche, die Datei zum schreiben zu öffnen
	try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.datei))) {

	    // Mit welchem Zeichen werden auf diesem System Zeilen getrennt?
	    String lineSep = System.getProperty("line.separator");

	    // schreibe zunächst die Überschrift
	    bw.write(ueberschrift+lineSep+lineSep);

	    // schreibe nun die Spaltenüberschriften
	    bw.write("Vorgangsnummer; Vorgangsbezeichnung; D; FAZ; FEZ; SAZ; SEZ; GP; FP"+lineSep);

	    // schreibe die einzelnen Vorgänge
	    for (Vorgang aktVorgang: plan.getVorgaenge()) {
		bw.write(aktVorgang.toString()+lineSep);
	    }
	    bw.write(lineSep);

	    // schreibe nun die Anfangsvorgänge
	    bw.write("Anfangsvorgang: ");
	    List<Vorgang> anfangsVorgaenge = plan.getStartVorgaenge();
	    for (int i=0; i<anfangsVorgaenge.size(); i++) {
		bw.write(anfangsVorgaenge.get(i).getNummer()+(i<anfangsVorgaenge.size()-1?",":""));
	    }
	    bw.write(lineSep);

	    // schreibe nun die Endvorgänge
	    bw.write("Endvorgang: ");
	    List<Vorgang> endVorgaenge = plan.getEndVorgaenge();
	    for (int i=0; i<endVorgaenge.size(); i++) {
		bw.write(endVorgaenge.get(i).getNummer()+(i<endVorgaenge.size()-1?",":""));
	    }
	    bw.write(lineSep);

	    // schreibe die Gesamtdauer
	    int dauer = plan.getDauer();
	    bw.write("Gesamtdauer: ");
	    if (dauer == -1) {
		bw.write("Nicht eindeutig");
	    } else {
		bw.write(dauer+"");
	    }
	    bw.write(lineSep+lineSep);

	    // schreibe die kritischen Pfade
	    List<List<Integer>> pfade = plan.getKritischePfade();
	    bw.write((pfade.size()<=1?"Kritischer Pfad":"Kritische Pfade"));
	    bw.write(lineSep);
	    for (List<Integer> aktPfad: pfade) {
		for (int i = 0; i < aktPfad.size(); i++) {
		    bw.write(aktPfad.get(i)+(i<aktPfad.size()-1?"->":""));
		}
		bw.write(lineSep);
	    }

	} catch (IOException e) {
	    // leite die Exception mit deutscher Fehlermeldung weiter
	    throw new IOException("Die angegebene Ausgabedatei "+this.datei+" kann nicht beschrieben oder erzeugt werden!");
	}
    }
}
