package netzplanerstellung.logik;

import java.util.List;
import java.util.ArrayList;

public class Vorgang {
    private int nummer;
    private String bezeichnung;
    private int dauer;
    private List<Integer> vorgaenger;
    private List<Integer> nachfolger;
    private int faz;
    private int fez;
    private int saz;
    private int sez;
    private int gp;
    private int fp;

    public Vorgang(int nummer, String bezeichnung, int dauer) {
	this.nummer = nummer;
	this.bezeichnung = bezeichnung;
	this.dauer = dauer;
	this.vorgaenger = new ArrayList<>();
	this.nachfolger = new ArrayList<>();
    }

    public void addVorgaenger(int vorgaenger) {
	this.vorgaenger.add(vorgaenger);
    }

    public void addNachfolger(int nachfolger) {
	this.nachfolger.add(nachfolger);
    }

    public boolean istKritisch() {
	return this.gp == 0 && this.fp == 0;
    }

    public String toString() {
	StringBuilder res = new StringBuilder();
	res.append(nummer+"; ");
	res.append(bezeichnung+"; ");
	res.append(dauer+"; ");
	res.append(faz+"; ");
	res.append(fez+"; ");
	res.append(saz+"; ");
	res.append(sez+"; ");
	res.append(gp+"; ");
	res.append(fp+"");
	return res.toString();
    }

    public int getNummer() {
	return this.nummer;
    }

    public List<Integer> getNachfolger() {
	return this.nachfolger;
    }

    public List<Integer> getVorgaenger() {
	return this.vorgaenger;
    }
}
