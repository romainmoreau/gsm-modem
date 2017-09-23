package fr.romainmoreau.gsmmodem.client.api;

public class GsmModemResponseException extends GsmModemException {
	private static final long serialVersionUID = 1L;

	public GsmModemResponseException(String message) {
		super(message);
	}

	public GsmModemResponseException(String message, Exception cause) {
		super(message, cause);
	}
}
