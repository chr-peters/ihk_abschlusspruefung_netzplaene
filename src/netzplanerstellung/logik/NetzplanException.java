package netzplanerstellung.logik;

/**
 * Diese Exception wird verwendet, um logische Fehler im Netzplan zu behandeln.
 */
public class NetzplanException extends Exception {
    public NetzplanException(String msg) {
	super(msg);
    }
    public NetzplanException() {
	super("Ein Fehler im Netzplan wurde gefunden!");
    }
}
