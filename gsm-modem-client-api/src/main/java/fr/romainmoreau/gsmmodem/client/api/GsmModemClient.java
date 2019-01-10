package fr.romainmoreau.gsmmodem.client.api;

import java.io.Closeable;
import java.io.IOException;

public interface GsmModemClient extends Closeable {
	void setSMSTextMode() throws IOException, GsmModemException;

	void sendSms(String gsmNumber, String sms) throws IOException, GsmModemException;

	void sendCommand(String command) throws IOException, GsmModemException;
}
