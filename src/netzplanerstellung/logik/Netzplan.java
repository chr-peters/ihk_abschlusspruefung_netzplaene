package netzplanerstellung.logik;

import java.util.List;
import java.util.ArrayList;

public class Netzplan {
    private List<Vorgang> vorgaenge;
    private int [][] adjazenzen;
    private List<Integer> startKnoten;
    private List<Integer> endKnoten;

    public Netzplan(List<Vorgang> vorgaenge) {
	this.vorgaenge = vorgaenge;
	this.adjazenzen = new int [vorgaenge.size()][vorgaenge.size()];
	this.startKnoten = new ArrayList<>();
	this.endKnoten = new ArrayList<>();
    }

    public int getDauer() {
	return 0;
    }

    public List<List<Integer>> getKritischePfade() {
	return null;
    }

    private void erzeugeAdjazenzen() {

    }

    private boolean istZyklenfrei() {
	return false;
    }

    private boolean istZusammenhaengend() {
	return false;
    }
}
