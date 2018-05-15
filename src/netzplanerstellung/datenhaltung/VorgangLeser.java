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
	    // wird für die Fehlerbehandlung verwendet
	    int zeilenNr = 1;

	    // die Resultate werden zunächst in einer Map Vorgangsnummer -> Vorgang gespeichert,
	    // damit leicht geprüft werden kann, ob eine Vorgangsnummer doppelt vorkommt (dies wäre ein Fehlerfall)
	    Map<Integer, Vorgang> resultat = new HashMap<>();

	    // solange eine weitere Zeile existiert
	    while (aktZeile != null) {

		// teste, ob es sich um eine Überschrift handelt
		if (aktZeile.startsWith("//+")) {

		    // die Überschrift wurde noch nicht gesetzt
		    if (this.ueberschrift == null) {

			// extrahiere die Überschrift und trenne nicht sichtbare Zeichen ab
			String tmpUeberschrift = aktZeile.substring(3).trim();

			if (tmpUeberschrift.equals("")) {

			    // Fehler! Leere Überschriften sind ungültig!
			    throw new DateiFormatException(zeilenNr, "Leere Überschriften sind ungültig!");
			} else {
			    this.ueberschrift = tmpUeberschrift;
			}
		    } else {

			// es existiert bereits eine Überschrift - die Datei ist ungültig!
			throw new DateiFormatException(zeilenNr, "Nur eine Überschift pro Datei erlaubt!");
		    }
		} else if (aktZeile.startsWith("//")) {
		    // es handelt sich um eine Kommentarzeile
		    // hier ist keine Aktion erforderlich
		} else {
		    // es handelt sich um eine Datenzeile
		    
		    // trenne die Zeile anhand des Semikolons auf
		    String daten [] = aktZeile.trim().split(";");
		    
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

		    // teste, ob die Vorgangsnummer eindeutig ist
		    if (resultat.containsKey(tmpNummer)) {
			throw new DateiFormatException(zeilenNr, "Vorgangsnummer "+tmpNummer+" mehrfach vorhanden!");
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

		    // erzeuge den bis hierhin ausgelesenen Vorgang
		    Vorgang tmpVorgang = new Vorgang(tmpNummer, tmpBezeichnung, tmpDauer);

		    // lese nun die Vorgänger aus
		    if (!daten[3].trim().equals("-")) {
			// es sind Vorgänger vorhanden
			String vorgaengerRoh [] = daten[3].trim().split(",");

			if (vorgaengerRoh.length == 0) {
			    throw new DateiFormatException(zeilenNr, "Ungültige Angabe der Vorgänger "+daten[3]);
			}

			// fuege die Vorgänger nun sukzessive hinzu
			for (String aktVorgaenger: vorgaengerRoh) {
			    int tmpVorgaenger;
			    try {
				tmpVorgaenger = Integer.parseInt(aktVorgaenger.trim());
			    } catch (NumberFormatException e) {
				throw new DateiFormatException(zeilenNr, "Ungültiger Vorgänger "+aktVorgaenger);
			    }
			    tmpVorgang.addVorgaenger(tmpVorgaenger);
			}
		    }

		    // lese nun die Nachfolger aus
		    if (!daten[4].trim().equals("-")) {
			// es sind Nachfolger vorhanden
			String nachfolgerRoh [] = daten[4].trim().split(",");

			if (nachfolgerRoh.length == 0) {
			    throw new DateiFormatException(zeilenNr, "Ungültige Angabe der Nachfolger "+daten[4]);
			}

			// fuege die Nachfolger nun sukzessive hinzu
			for (String aktNachfolger: nachfolgerRoh) {
			    int tmpNachfolger;
			    try {
				tmpNachfolger = Integer.parseInt(aktNachfolger.trim());
			    } catch (NumberFormatException e) {
				throw new DateiFormatException(zeilenNr, "Ungültiger Nachfolger "+aktNachfolger);
			    }
			    tmpVorgang.addNachfolger(tmpNachfolger);
			}
		    }

		    // fuege nun den Neuen Vorgang dem Resultat hinzu
		    resultat.put(tmpNummer, tmpVorgang);
		}

		// lese die nächste Zeile
		aktZeile = br.readLine();
		zeilenNr++;
	    }
	    
	    // pruefe, ob eine Überschrift gefunden wurde
	    if (this.ueberschrift == null) {
		throw new DateiFormatException("Fehler beim Einlesen: Keine Überschrift gefunden!");
	    }

	    // speichere das Resultat in der entsprechenden Variable
	    this.vorgaenge = new ArrayList<>(resultat.values());

	} catch (FileNotFoundException e) {
	    // die Datei existiert nicht, ist ein Verzeichnis oder ist nicht lesbar
	    // reiche die Fehlermeldung mit deutschem Text weiter
	    throw new FileNotFoundException("Die angegebene Eingabedatei "+datei+" existiert nicht,"+
					    " ist ein Verzeichnis oder ist nicht lesbar!");
	} catch (IOException e) {
	    // Fehler beim Schliessen des BufferedReader
	    // Sollte in der Praxis nicht vorkommen, da die br.close()-Methode
	    // automatisch aufgerufen wird
	    throw new IOException("Ein schwerwiegender Fehler beim Lesen der Eingabedatei ist aufgetreten. Bitte kontaktieren Sie sofort den Entwickler!");
	}
    }
}
