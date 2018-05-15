package netzplanerstellung.datenhaltung;

/**
 * Diese Exception dient der Behandlung von Fehlern, welche das
 * Dateiformat betreffen.
 */
public class DateiFormatException extends Exception{
    public DateiFormatException(int zeile, String msg) {
	super("Fehler beim Einlesen in Zeile "+zeile+": "+msg);
    }
    public DateiFormatException(String msg) {
	super(msg);
    }
    public DateiFormatException() {
	super("Das Dateiformat ist ungültig!");
    }
}
