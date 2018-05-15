package netzplanerstellung.logik;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Netzplan {
    private List<Vorgang> vorgaenge;
    private int [][] adjazenzen;
    private List<Integer> startKnoten;
    private List<Integer> endKnoten;

    // Abbildung externe Nummern -> interne Nummern
    private Map<Integer, Integer> toInternal;
    // Abbildung interne Nummern -> externe Nummern
    private Map<Integer, Integer> fromInternal;

    public Netzplan(List<Vorgang> vorgaenge) throws NetzplanException{
	this.vorgaenge = vorgaenge;
	this.adjazenzen = new int [vorgaenge.size()][vorgaenge.size()];
	this.startKnoten = new ArrayList<>();
	this.endKnoten = new ArrayList<>();

	// um mit der Adjazenzmatrix arbeiten zu können, erzeuge zunaechst die Abbildung
	// zwischen den internen und externen Vorgangsnummern
	this.toInternal = new HashMap<>();
	this.fromInternal = new HashMap<>();
	for (int internal = 0; internal < this.vorgaenge.size(); internal++) {
	    toInternal.put(this.vorgaenge.get(internal).getNummer(), internal);
	    fromInternal.put(internal, this.vorgaenge.get(internal).getNummer());
	}

	// belege nun die Listen der Start- und Endknoten
	for (Vorgang aktVorgang: vorgaenge) {
	    if (aktVorgang.getVorgaenger().size() == 0) {
		this.startKnoten.add(toInternal.get(aktVorgang.getNummer()));
	    }
	    if (aktVorgang.getNachfolger().size() == 0) {
		this.endKnoten.add(toInternal.get(aktVorgang.getNummer()));
	    }
	}

	// teste, ob mindestens ein Startknoten existiert
	if (this.startKnoten.size() == 0) {
	    throw new NetzplanException("Fehler bei der Erstellung des Netzplans: Es existiert kein Startvorgang!");
	}

	// teste, ob mindestens ein Endknoten existiert
	if (this.endKnoten.size() == 0) {
	    throw new NetzplanException("Fehler bei der Erstellung des Netzplans: Es existiert kein Endvorgang!");
	}

	// erzeuge die Adjazenzmatrix
	// hierbei wird auch die Konsistenz der Beziehungen unter den Vorgängen sichergestellt
	this.erzeugeAdjazenzen();

	// teste, ob der Graph auch zusammen haengt
	// es wird eine Exception geworfen, wenn dies nicht der Fall ist
	this.istZusammenhaengend();

	// teste, ob der Graph auch zyklenfrei ist
	// es wird eine Exception geworfen, wenn dies nicht der Fall ist
	this.istZyklenfrei();

	// beginne mit Phase 1: Vorwaertsrechnung
	this.vorwaertsRechnung();

	/**
	 * DEBUG: Gebe die Adjazenzmatrix aus
	 */
	// for (int zeile=0; zeile<adjazenzen.length; zeile++) {
	//     for (int spalte=0; spalte<adjazenzen[0].length; spalte++) {
	// 	System.out.print(this.adjazenzen[zeile][spalte]+" ");
	//     }
	//     System.out.print("\n");
	// }

    }

    private void vorwaertsRechnung() {
	
    }

    public int getDauer() {
	return 0;
    }

    public List<List<Integer>> getKritischePfade() {
	return null;
    }

    private void erzeugeAdjazenzen() throws NetzplanException {
	for (Vorgang aktVorgang: this.vorgaenge) {
	    // die Zeile entspricht der internen Nummer des Vorgangs
	    int zeile = toInternal.get(aktVorgang.getNummer());
	    for (int aktNachfolger: aktVorgang.getNachfolger()) {
		// teste zunächst, ob aktNachfolger überhaupt ein gültiger Vorgang ist
		if (!toInternal.containsKey(aktNachfolger)) {
		    throw new NetzplanException("Fehler bei der Erstellung des Netzplans: "+
						"Vorgang "+aktVorgang.getNummer()+" hat Vorgang "+
						aktNachfolger+" als Nachfolger, obwohl dieser nicht existiert!");
		}

		// teste, ob die Beziehung konsistent ist
		if (!this.vorgaenge.get(toInternal.get(aktNachfolger)).getVorgaenger().contains(aktVorgang.getNummer())){
		    // die Beziehung ist nicht konsistent, da aktVorgang nicht Vorgänger von aktNachfolger ist
		    throw new NetzplanException("Fehler bei der Erstellung des Netzplans: "+
						"Inkonsistente Beziehung gefunden! Vorgang "+aktVorgang.getNummer()+
						" hat Vorgang "+aktNachfolger+" als Nachfolger, ist aber selbst nicht "+
						"Vorgänger von diesem!");
		}
		// die Spalte entspricht der internen Nummer des Nachfolgers
		int spalte = toInternal.get(aktNachfolger);
		this.adjazenzen[zeile][spalte] = 1;
	    }
	}

	// Die Adjazensmatrix konnte aufgebaut werden, was garantiert, dass die Vorgänger -> Nachfolger Beziehung konsistent ist.
	// Es muss aber noch geprüft werden, ob die Nachfolger -> Vorgaenger Beziehung ebenfalls konsistent ist!
	// Dies erfolgt an dieser Stelle.
	for (Vorgang aktVorgang: this.vorgaenge) {
	    for (int aktVorgaenger: aktVorgang.getVorgaenger()) {
		// teste zunaechst, ob aktVorgänger ueberhaupt ein gültiger Vorgang ist
		if (!toInternal.containsKey(aktVorgaenger)) {
		    throw new NetzplanException("Fehler bei der Erstellung des Netzplans: "+
						"Vorgang "+aktVorgang.getNummer()+" hat Vorgang "+
						aktVorgaenger+" als Vorgänger, obwohl dieser nicht existiert!");
		}

		if (!this.vorgaenge.get(toInternal.get(aktVorgaenger)).getNachfolger().contains(aktVorgang.getNummer())) {
		    // die Beziehung ist nicht konsistent, da aktVorgang nicht Nachfolger von aktVorgänger ist
		    throw new NetzplanException("Fehler bei der Erstellung des Netzplans: "+
						"Inkonsistente Beziehung gefunden! Vorgang "+aktVorgang.getNummer()+
						" hat Vorgang "+aktVorgaenger+" als Vorgänger, ist aber selbst nicht "+
						"Nachfolger von diesem!");
		}
	    }
	}
    }

    private boolean istZyklenfrei() throws NetzplanException{
	// Zum Auffinden der Zyklen wird ausgehend von jedem Startknoten jeder moegliche Pfad erzeugt (Expansion des Graphen)
	// Werden Knoten in einen Pfad neu aufgenommen, die in diesem Pfad bereits existieren, ist der Graph
	// nicht zyklenfrei.

	for (int aktStartknoten: this.startKnoten) {
	    // die Liste mit den Pfaden der Expansion
	    List<List<Integer>> pfade = new ArrayList<>();
	    
	    // der Startpfad enthaelt nur den Startknoten
	    List<Integer> startPfad = new ArrayList<>();
	    startPfad.add(aktStartknoten);

	    pfade.add(startPfad);

	    // beginne mit der Expansion
	    while (pfade.size() > 0) {
		// entferne den aktuellen Pfad
		List<Integer> aktPfad = pfade.remove(0);

		// betrachte den letzten Knoten des Pfades
		int aktKnoten = aktPfad.get(aktPfad.size()-1);

		// wenn es ein Endknoten ist, so ist dieser Pfad frei von Zyklen und muss nicht weiter betrachtet werden
		if (this.endKnoten.contains(aktKnoten)) {
		    continue;
		}

		// betrachte nun alle Nachbarn (Kinder) des letzten Knotens
		List<Integer> kinder = new ArrayList<>();
		for (int i = 0; i < this.adjazenzen[aktKnoten].length; i++) {
		    if (this.adjazenzen[aktKnoten][i] == 1) {
			kinder.add(i);
		    }
		}

		// teste fuer jedes Kind, ob es schon Teil des Pfades ist (dann liegt ein Zyklus vor)
		// falls nicht, erzeuge einen neuen Pfad mit dem jeweiligen Kind als Endknoten
		for (int aktKind: kinder) {
		    if (aktPfad.contains(aktKind)) {
			// Zyklus gefunden! Erzeuge Fehlertext
			StringBuilder fehlertext = new StringBuilder();
			fehlertext.append("Fehler bei der Erstellung des Netzplans: Es wurde ein Zyklus erkannt!\n");

			// zeigt das erste Vorkommen des Kindknoten im Zyklus an
			boolean erstesVorkommen = false;
			for (int zyklKnoten: aktPfad) {
			    if (!erstesVorkommen && zyklKnoten == aktKind) {
				erstesVorkommen = true;
			    }
			    if (erstesVorkommen) {
				fehlertext.append(fromInternal.get(zyklKnoten)+"->");
			    }
			}
			fehlertext.append(fromInternal.get(aktKind));
			throw new NetzplanException(fehlertext.toString());
		    } else {
			List<Integer> neuerPfad = new ArrayList<>(aktPfad);
			neuerPfad.add(aktKind);
			pfade.add(neuerPfad);
		    }
		}
	    }
	}
	return true;
    }

    private boolean istZusammenhaengend() throws NetzplanException{
	// Um zu pruefen, ob der Graph zusammenhaengend ist, wird dieser als ungerichtet
	// aufgefasst und beginnend beim ersten Knoten traversiert.
	// Wenn Knoten am Ende der Traversierung nicht erreicht werden konnten, ist der
	// Graph nicht zusammenhaengend

	// die symmetrische Adjazenzmatrix
	int adjazenzenSym [][] = new int [this.adjazenzen.length][this.adjazenzen[0].length];

	// Zu diesem Zweck wird die Adjazenzmatrix zunaechst so erweitert, dass sie symmetrisch ist.
	for (int zeile = 0; zeile < this.adjazenzen.length; zeile++) {
	    for (int spalte = 0; spalte < this.adjazenzen[0].length; spalte++) {
		if (this.adjazenzen[zeile][spalte] == 1) {
		    adjazenzenSym[zeile][spalte] = 1;
		    adjazenzenSym[spalte][zeile] = 1;
		}
	    }
	}

	// Liste mit allen Knoten (die Knoten die hier am Ende uebrig bleiben, koennen nicht erreicht werden)
	List<Integer> alleKnoten = new ArrayList<>();
	for (int i=0; i<adjazenzenSym.length; i++) {
	    alleKnoten.add(i);
	}

	// Liste mit allen schon besuchten Knoten
	List<Integer> besucht = new ArrayList<>();

	// Liste mit den Knoten, deren Besuch unmittelbar ansteht
	List<Integer> aktKnoten = new ArrayList<>();

	// beginne bei Knoten 0 (Startknoten beliebig)
	aktKnoten.add(0);

	while(aktKnoten.size()>0) {
	    // entferne den aktuellen Knoten
	    int tmp = aktKnoten.remove(0);

	    // bestimme alle Knoten, die von diesem Knoten aus erreichbar sind
	    // und noch nicht besucht wurden und fuege sie zu aktKnoten hinzu
	    for (int i = 0; i<adjazenzenSym[tmp].length; i++) {
		if (adjazenzenSym[tmp][i]==1 && !besucht.contains(i)){
		    aktKnoten.add(i);
		}
	    }

	    // markiere den aktuellen Knoten als besucht
	    besucht.add(tmp);

	    // entferne den aktuellen Knoten aus der Menge aller Knoten
	    alleKnoten.remove(new Integer(tmp));
	}

	if (alleKnoten.size() != 0) {
	    // Es sind noch Knoten uebrig, also gebe eine Fehlermeldung
	    StringBuilder fehlertext = new StringBuilder();
	    fehlertext.append("Fehler bei der Erstellung des Netzplans: Der Netzplan ist nicht zusammenhängend!"+
			      " Es existiert kein ungerichteter Pfad zwischen Vorgang "+fromInternal.get(0)+
			      " und ");
	    if (alleKnoten.size() > 1) {
		fehlertext.append("den Vorgängen ");
	    }
	    for (int i=0; i<alleKnoten.size(); i++) {
		fehlertext.append(fromInternal.get(alleKnoten.get(i))+(i==alleKnoten.size()-1?"":", "));
	    }
	    throw new NetzplanException(fehlertext.toString());
	}
	
	return true;
    }
}
