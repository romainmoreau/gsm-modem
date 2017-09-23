package fr.romainmoreau.gsmmodem.client.api;

public class GsmModemException extends Exception {
	private static final long serialVersionUID = 1L;

	public GsmModemException() {
		super();
	}

	public GsmModemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GsmModemException(String message, Throwable cause) {
		super(message, cause);
	}

	public GsmModemException(String message) {
		super(message);
	}

	public GsmModemException(Throwable cause) {
		super(cause);
	}
}
