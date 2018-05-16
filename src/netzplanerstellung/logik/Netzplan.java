package netzplanerstellung.logik;

import java.util.List;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.HashMap;

/**
 * Klasse zur Darstellung eines Netzplans.
 */
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

	// um mit der Adjazenzmatrix arbeiten zu können, erzeuge zunächst die Abbildung
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

	// teste, ob der Graph auch zusammenhängt
	// es wird eine Exception geworfen, wenn dies nicht der Fall ist
	this.istZusammenhaengend();

	// teste, ob der Graph auch zyklenfrei ist
	// es wird eine Exception geworfen, wenn dies nicht der Fall ist
	this.istZyklenfrei();

	// beginne mit Phase 1: Vorwärtsrechnung
	this.vorwaertsRechnung();

	// fahre fort mit Phase 2: Rückwärtsrechnung
	this.rueckwaertsRechnung();

	// führe nun Phase 3 durch: Ermittlung der Zeitreserven
	this.zeitreserven();

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
	// erzeuge eine Queue der abzuarbeitenden Knoten (diese enthält zunächst nur alle Startknoten)
	Deque<Integer> abzuarbeiten = new ArrayDeque<>(this.startKnoten);

	while (abzuarbeiten.size() > 0) {
	    // entferne aktuellen Knoten
	    int aktKnoten = abzuarbeiten.removeFirst();

	    // besorge Referenz auf den zugehörigen Vorgang, um FEZ setzen zu können
	    Vorgang aktVorgang = this.vorgaenge.get(aktKnoten);
	    
	    // setze FEZ
	    aktVorgang.setFEZ(aktVorgang.getFAZ() + aktVorgang.getDauer());

	    // besuche die Nachbarn (Kinder) des aktuellen Vorgangs (Achtung, diese liegen in externer Darstellung vor!)
	    List<Integer> kinder = aktVorgang.getNachfolger();
	    for (int aktKind: kinder) {
		// bestimme interne Darstellung des Kindes
		int intern = toInternal.get(aktKind);

		// besorge Referenz auf das Kind
		Vorgang kindVorgang = this.vorgaenge.get(intern);

		// setze FAZ des Kindes, falls es sich hierdurch vergrößert
		if (kindVorgang.getFAZ() < aktVorgang.getFEZ()) {
		    kindVorgang.setFAZ(aktVorgang.getFEZ());
		}

		// füge das Kind hinten an die Queue an
		abzuarbeiten.addLast(intern);
	    }
	}
    }

    private void rueckwaertsRechnung() {
	// setze zuerst für jeden Endknoten SEZ=FEZ
	for (int aktKnoten: this.endKnoten) {
	    // besorge Referenz
	    Vorgang aktVorgang = this.vorgaenge.get(aktKnoten);
	    // setze Wert
	    aktVorgang.setSEZ(aktVorgang.getFEZ());
	}
	
	// erzeuge eine Queue der abzuarbeitenden Knoten (diese enthält zunaechst nur alle Endknoten)
	Deque<Integer> abzuarbeiten = new ArrayDeque<>(this.endKnoten);

	while (abzuarbeiten.size() > 0) {
	    // entferne den aktuellen Knoten
	    int aktKnoten = abzuarbeiten.removeFirst();
	    
	    // besorge Referenz
	    Vorgang aktVorgang = this.vorgaenge.get(aktKnoten);

	    // setze SAZ=SEZ-D
	    aktVorgang.setSAZ(aktVorgang.getSEZ()-aktVorgang.getDauer());

	    // besuche die Vorgänger (auch hier wieder externe Darstellung)
	    List<Integer> vorgaenger = aktVorgang.getVorgaenger();

	    for (int aktVorgaenger: vorgaenger) {
		// besorge Referenz
		Vorgang aktVorgaengerRef = this.vorgaenge.get(toInternal.get(aktVorgaenger));

		// setze SEZ des Vorgängers, wenn es noch nicht gesetzt wurde oder es sich verringert
		if (aktVorgaengerRef.getSEZ() == 0 || aktVorgaengerRef.getSEZ() > aktVorgang.getSAZ()) {
		    aktVorgaengerRef.setSEZ(aktVorgang.getSAZ());
		}

		// fuege den Vorgänger hinten an die Queue an
		abzuarbeiten.addLast(toInternal.get(aktVorgaenger));
	    }
	}
    }

    private void zeitreserven() {
	// Iteriere durch die Vorgänge und setze GP und FP
	for (Vorgang aktVorgang: this.vorgaenge) {
	    aktVorgang.setGP(aktVorgang.getSAZ() - aktVorgang.getFAZ());

	    // bei Endknoten ist FP immer 0
	    if (aktVorgang.getNachfolger().size() == 0) {
		aktVorgang.setFP(0);
		continue;
	    }

	    // finde nun das Minimum aus dem FAZ seiner Nachfolger
	    int minFAZ = Integer.MAX_VALUE;
	    for (int aktNachfolger: aktVorgang.getNachfolger()) {
		// besorge Referenz
		Vorgang aktNachfolgerRef = this.vorgaenge.get(toInternal.get(aktNachfolger));

		// setze neues minFAZ, falls FAZ des Nachfolgers geringer ist
		if (minFAZ > aktNachfolgerRef.getFAZ()) {
		    minFAZ = aktNachfolgerRef.getFAZ();
		}
	    }

	    // setze FP
	    aktVorgang.setFP(minFAZ - aktVorgang.getFEZ());
	}
    }

    /**
     * Die Dauer des Projektes ist der FEZ (bzw. SEZ) des letzten Vorganges.
     * Gibt es mehrere Endvorgänge und ist FEZ nicht einheitlich,
     * so ist dieser Wert nicht eindeutig (in diesem Fall wird der Wert -1 zurückgegeben).
     */
    public int getDauer() {
	int tmpDauer = this.vorgaenge.get(this.endKnoten.get(0)).getFEZ();
	for (int curEndKnoten: this.endKnoten) {
	    if (this.vorgaenge.get(curEndKnoten).getFEZ() != tmpDauer) {
		return -1;
	    }
	}
	return tmpDauer;
    }

    public List<List<Integer>> getKritischePfade() {
	// erzeuge leeres Resultat
	List<List<Integer>> resultat = new ArrayList<>();

	// bestimme kritische Pfade ausgehend von jedem Startknoten
	for (int aktStartKnoten: this.startKnoten) {
	    
	    // wenn der Knoten nicht kritisch ist, ist nichts zu tun
	    if (!this.vorgaenge.get(aktStartKnoten).istKritisch()) {
		continue;
	    }

	    // erzeuge die Queue aus Pfaden
	    // die Bezeichner der Knoten sind hierbei in externer Darstellung angegeben
	    Deque<List<Integer>> pfade = new ArrayDeque<>();

	    // fuege Pfad mit aktStartKnoten der Liste an
	    List<Integer> startPfad = new ArrayList<>();
	    startPfad.add(fromInternal.get(aktStartKnoten));
	    pfade.add(startPfad);

	    while(pfade.size() > 0) {
		List<Integer> aktPfad = pfade.removeFirst();

		// betrachte den letzten Knoten aus aktPfad
		int aktKnoten = aktPfad.get(aktPfad.size()-1);

		// ist aktPfad kritischer Pfad (ist sein letzter Knoten ein Endknoten)?
		if (this.endKnoten.contains(toInternal.get(aktKnoten))) {
		    // dann füge aktPfad dem Resultat hinzu
		    resultat.add(aktPfad);
		    continue;
		}

		// generiere alle kritischen Nachfolger des letzten Elementes aus aktpfad
		List<Integer> nachfolger = this.vorgaenge.get(toInternal.get(aktKnoten)).getNachfolger();
		for (int aktNachfolger: nachfolger) {
		    // handelt es sich um einen kritischen Nachfolger?
		    if (this.vorgaenge.get(toInternal.get(aktNachfolger)).istKritisch()) {
			// erzeuge einen neuen Pfad mit diesem als letztes Element
			List<Integer> neuerPfad = new ArrayList<>(aktPfad);
			neuerPfad.add(aktNachfolger);

			// füge diesen Pfad den zu bearbeitenden Pfaden hinzu
			pfade.addLast(neuerPfad);
		    }
		}
	    }
	}

	return resultat;
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

    /**
     * Zum Auffinden der Zyklen wird ausgehend von jedem Startknoten jeder mögliche Pfad erzeugt (Expansion des Graphen)
     * Werden Knoten in einen Pfad neu aufgenommen, die in diesem Pfad bereits existieren, ist der Graph
     * nicht zyklenfrei.
     */
    private boolean istZyklenfrei() throws NetzplanException{
	for (int aktStartknoten: this.startKnoten) {
	    // die Queue mit den Pfaden der Expansion
	    Deque<List<Integer>> pfade = new ArrayDeque<>();
	    
	    // der Startpfad enthält nur den Startknoten
	    List<Integer> startPfad = new ArrayList<>();
	    startPfad.add(aktStartknoten);

	    pfade.add(startPfad);

	    // beginne mit der Expansion
	    while (pfade.size() > 0) {
		// entferne den aktuellen Pfad
		List<Integer> aktPfad = pfade.removeFirst();

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

		// teste für jedes Kind, ob es schon Teil des Pfades ist (dann liegt ein Zyklus vor)
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
			pfade.addLast(neuerPfad);
		    }
		}
	    }
	}
	return true;
    }

    /** 
     * Um zu prüfen, ob der Graph zusammenhängend ist, wird dieser als ungerichtet
     * aufgefasst und beginnend beim ersten Knoten traversiert.
     * Wenn Knoten am Ende der Traversierung nicht erreicht werden konnten, ist der
     * Graph nicht zusammenhaengend.
     */
    private boolean istZusammenhaengend() throws NetzplanException{
	// Zu diesem Zweck wird die Adjazenzmatrix zunächst so erweitert, dass sie symmetrisch ist.
	int adjazenzenSym [][] = new int [this.adjazenzen.length][this.adjazenzen[0].length];

	for (int zeile = 0; zeile < this.adjazenzen.length; zeile++) {
	    for (int spalte = 0; spalte < this.adjazenzen[0].length; spalte++) {
		if (this.adjazenzen[zeile][spalte] == 1) {
		    adjazenzenSym[zeile][spalte] = 1;
		    adjazenzenSym[spalte][zeile] = 1;
		}
	    }
	}

	// Liste mit allen Knoten (die Knoten die hier am Ende übrig bleiben, können nicht erreicht werden)
	List<Integer> alleKnoten = new ArrayList<>();
	for (int i=0; i<adjazenzenSym.length; i++) {
	    alleKnoten.add(i);
	}

	// Liste mit allen schon besuchten Knoten
	List<Integer> besucht = new ArrayList<>();

	// Queue mit den Knoten, deren Besuch unmittelbar ansteht
	Deque<Integer> aktKnoten = new ArrayDeque<>();

	// beginne bei Knoten 0 (Startknoten beliebig)
	aktKnoten.add(0);

	while(aktKnoten.size()>0) {
	    // entferne den aktuellen Knoten
	    int tmp = aktKnoten.removeFirst();

	    // bestimme alle Knoten, die von diesem Knoten aus erreichbar sind
	    // und noch nicht besucht wurden und füge sie zu aktKnoten hinzu
	    for (int i = 0; i<adjazenzenSym[tmp].length; i++) {
		if (adjazenzenSym[tmp][i]==1 && !besucht.contains(i)){
		    aktKnoten.addLast(i);
		}
	    }

	    // markiere den aktuellen Knoten als besucht
	    besucht.add(tmp);

	    // entferne den aktuellen Knoten aus der Menge aller Knoten (Vorsicht wegen verschiedener Signaturen von remove)
	    alleKnoten.remove(new Integer(tmp));
	}

	if (alleKnoten.size() != 0) {
	    // Es sind noch Knoten übrig, also gebe eine Fehlermeldung (-> der Graph ist nicht zusammenhängend)
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

    public List<Vorgang> getVorgaenge() {
	return this.vorgaenge;
    }

    public List<Vorgang> getStartVorgaenge() {
	List<Vorgang> resultat = new ArrayList<>();
	for (int aktVorgang: this.startKnoten) {
	    resultat.add(this.vorgaenge.get(aktVorgang));
	}
	return resultat;
    }

    public List<Vorgang> getEndVorgaenge() {
	List<Vorgang> resultat = new ArrayList<>();
	for (int aktVorgang: this.endKnoten) {
	    resultat.add(this.vorgaenge.get(aktVorgang));
	}
	return resultat;
    }
}
