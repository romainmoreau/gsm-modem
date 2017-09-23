package fr.romainmoreau.gsmmodem.client.api;

import java.io.Closeable;
import java.io.IOException;

public interface GsmModemClient extends Closeable {
	void sendSms(String gsmNumber, String sms) throws IOException, GsmModemException;
}
