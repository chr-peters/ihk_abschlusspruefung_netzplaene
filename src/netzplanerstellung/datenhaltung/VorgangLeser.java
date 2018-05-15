package netzplanerstellung.datenhaltung;

import netzplanerstellung.logik.Vorgang;

import java.io.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VorgangLeser {
    private List<Vorgang> vorgaenge;
    private String ueberschrift;

    public VorgangLeser(String datei) throws FileNotFoundException, IOException, DateiFormatException {
	this.vorgaenge = new ArrayList<>();
	this.leseVorgaenge(datei);
    }

    public List<Vorgang> getVorgaenge() {
	return this.vorgaenge;
    }

    public String getUeberschrift() {
	return this.ueberschrift;
    }

    private void leseVorgaenge(String datei) throws FileNotFoundException, IOException, DateiFormatException{
	// versuche, die Datei zum lesen zu öffnen
	try (BufferedReader br = new BufferedReader(new FileReader(datei))) {
	    // die aktuelle Zeile
	    String aktZeile = br.readLine();

	    // die Nummer der aktuellen Zeile
	    // wird fuer die Fehlerbehandlung verwendet
	    int zeilenNr = 1;

	    // die Resultate werden zunaechst in einer Map Vorgangsnummer -> Vorgang gespeichert,
	    // damit leicht geprueft werden kann, ob eine Vorgangsnummer doppelt vorkommt (dies waere ein Fehlerfall)
	    Map<Integer, Vorgang> resultat = new HashMap<>();

	    // solange eine weitere Zeile existiert
	    while (aktZeile != null) {

		// teste, ob es sich um eine Überschrift handelt
		if (aktZeile.startsWith("//+")) {

		    // die Ueberschrift wurde noch nicht gesetzt
		    if (this.ueberschrift == null) {

			// extrahiere die Ueberschrift und trenne nicht sichtbare Zeichen ab
			String tmpUeberschrift = aktZeile.substring(3).trim();

			if (tmpUeberschrift.equals("")) {

			    // Fehler! Leere Ueberschriften sind ungueltig!
			    throw new DateiFormatException(zeilenNr, "Leere Überschriften sind ungültig!");
			} else {
			    this.ueberschrift = tmpUeberschrift;
			}
		    } else {

			// es existiert bereits eine Ueberschrift - die Datei ist ungueltig!
			throw new DateiFormatException(zeilenNr, "Nur eine Überschift pro Datei erlaubt!");
		    }
		} else if (aktZeile.startsWith("//")) {
		    // es handelt sich um eine Kommentarzeile
		    // hier ist keine Aktion erforderlich
		} else {
		    // es handelt sich um eine Datenzeile
		    
		    // trenne die Zeile anhand des Semikolons auf
		    String daten [] = aktZeile.split(";");
		    
		    // teste, ob die Anzahl der Spalten korrekt ist
		    if (daten.length != 5) {
			throw new DateiFormatException(zeilenNr, "Erwarte 5 Elemente pro Zeile, "+daten.length+" erhalten.");
		    }

		    // versuche, die Vorgangsnummer auszulesen
		    int tmpNummer;
		    try {
			tmpNummer = Integer.parseInt(daten[0].trim());
		    } catch (NumberFormatException e) {
			throw new DateiFormatException(zeilenNr, "Ungültige Vorgangsnummer "+daten[0]);
		    }

		    // versuche, die Vorgangsbezeichnung auszulesen
		    String tmpBezeichnung = daten[1].trim();
		    if (tmpBezeichnung.equals("")) {
			throw new DateiFormatException(zeilenNr, "Keine leere Vorgangsbezeichnung erlaubt!");
		    }

		    // versuche, die Dauer des Vorgangs auszulesen
		    int tmpDauer;
		    try {
			tmpDauer = Integer.parseInt(daten[2].trim());
		    } catch (NumberFormatException e) {
			throw new DateiFormatException(zeilenNr, "Ungültige Vorgangsdauer "+daten[2]);
		    }
		    // negative Dauern und Dauern=0 sind nicht erlaubt
		    if (tmpDauer <= 0) {
			throw new DateiFormatException(zeilenNr, "Dauern <= 0 sind nicht erlaubt!");
		    }
		}

		// lese die nächste Zeile
		aktZeile = br.readLine();
		zeilenNr++;
	    }
	    
	    // pruefe, ob eine Ueberschrift gefunden wurde
	    if (this.ueberschrift == null) {
		throw new DateiFormatException("Fehler beim Einlesen: Keine Überschrift gefunden!");
	    }
	} catch (FileNotFoundException e) {
	    // die Datei existiert nicht, ist ein Verzeichnis oder ist nicht lesbar
	    // reiche die Fehlermeldung weiter
	    throw e;
	} catch (IOException e) {
	    // Fehler beim Schliessen des BufferedReader
	    // Sollte in der Praxis nicht vorkommen, da die br.close()-Methode
	    // automatisch aufgerufen wird
	    throw e;
	}
    }
}
