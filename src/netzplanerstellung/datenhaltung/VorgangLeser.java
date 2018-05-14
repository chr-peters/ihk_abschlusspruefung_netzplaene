package netzplanerstellung.datenhaltung;

import netzplanerstellung.logik.Vorgang;

import java.util.List;
import java.util.ArrayList;

public class VorgangLeser {
    private List<Vorgang> vorgaenge;
    private String ueberschrift;

    public VorgangLeser(String datei) {
	this.vorgaenge = new ArrayList<>();
	this.leseVorgaenge(datei);
    }

    public List<Vorgang> getVorgaenge() {
	return this.vorgaenge;
    }

    public String getUeberschrift() {
	return this.ueberschrift;
    }

    private void leseVorgaenge(String datei) {

    }
}
