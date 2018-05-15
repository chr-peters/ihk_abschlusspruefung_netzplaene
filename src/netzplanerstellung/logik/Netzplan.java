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

	// um mit der Adjazenzmatrix arbeiten zu koennen, erzeuge zunaechst die Abbildung
	// zwischen den internen und externen Vorgangsnummern
	this.toInternal = new HashMap<>();
	this.fromInternal = new HashMap<>();
	for (int internal = 0; internal < this.vorgaenge.size(); internal++) {
	    toInternal.put(this.vorgaenge.get(internal).getNummer(), internal);
	    fromInternal.put(internal, this.vorgaenge.get(internal).getNummer());
	}

	// erzeuge die Adjazenzmatrix
	// hierbei wird auch die Konsistenz der Beziehungen unter den Vorgaengen sichergestellt
	this.erzeugeAdjazenzen();
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
		// teste zunaechst, ob aktNachfolger ueberhaupt ein gueltiger Vorgang ist
		if (!toInternal.containsKey(aktNachfolger)) {
		    throw new NetzplanException("Fehler bei der Erstellung des Netzplans: "+
						"Vorgang "+aktVorgang.getNummer()+" hat Vorgang "+
						aktNachfolger+" als Nachfolger, obwohl dieser nicht existiert!");
		}

		// teste, ob die Beziehung konsistent ist
		if (!this.vorgaenge.get(toInternal.get(aktNachfolger)).getVorgaenger().contains(aktVorgang.getNummer())){
		    // die Beziehung ist nicht konsistent, da aktVorgang nicht Vorgaenger von aktNachfolger ist
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

	// Die Adjazensmatrix konnte aufgebaut werden, was garantiert, dass die Vorgaenger -> Nachfolger Beziehung konsistent ist.
	// Es muss aber noch geprueft werden, ob die Nachfolger -> Vorgaenger Beziehung ebenfalls konsistent ist!
	// Dies erfolgt an dieser Stelle.
	for (Vorgang aktVorgang: this.vorgaenge) {
	    for (int aktVorgaenger: aktVorgang.getVorgaenger()) {
		// teste zunaechst, ob aktVorgaenger ueberhaupt ein gueltiger Vorgang ist
		if (!toInternal.containsKey(aktVorgaenger)) {
		    throw new NetzplanException("Fehler bei der Erstellung des Netzplans: "+
						"Vorgang "+aktVorgang.getNummer()+" hat Vorgang "+
						aktVorgaenger+" als Vorgänger, obwohl dieser nicht existiert!");
		}

		if (!this.vorgaenge.get(toInternal.get(aktVorgaenger)).getNachfolger().contains(aktVorgang.getNummer())) {
		    // die Beziehung ist nicht konsistent, da aktVorgang nicht Nachfolger von aktVorgaenger ist
		    throw new NetzplanException("Fehler bei der Erstellung des Netzplans: "+
						"Inkonsistente Beziehung gefunden! Vorgang "+aktVorgang.getNummer()+
						" hat Vorgang "+aktVorgaenger+" als Vorgänger, ist aber selbst nicht "+
						"Nachfolger von diesem!");
		}
	    }
	}
    }

    private boolean istZyklenfrei() {
	return false;
    }

    private boolean istZusammenhaengend() {
	return false;
    }
}
