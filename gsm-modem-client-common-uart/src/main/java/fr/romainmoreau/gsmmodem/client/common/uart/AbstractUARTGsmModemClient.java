package fr.romainmoreau.gsmmodem.client.common.uart;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import fr.romainmoreau.gsmmodem.client.api.GsmModemException;
import fr.romainmoreau.gsmmodem.client.api.GsmModemResponseException;
import fr.romainmoreau.gsmmodem.client.common.AbstractGsmModemClient;

public abstract class AbstractUARTGsmModemClient extends AbstractGsmModemClient {
	private final long timeout;

	public AbstractUARTGsmModemClient(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public synchronized void sendSms(String gsmNumber, String sms) throws IOException, GsmModemException {
		sendString("AT+CMGF=1" + StringUtils.CR);
		waitForResponseToContain("OK");
		sendString("AT+CMGS=\"" + gsmNumber + "\"" + StringUtils.CR);
		waitForResponseToContain("> ");
		sendString(sms + (char) 26);
		waitForResponseToContain("OK");
	}

	public long getTimeout() {
		return timeout;
	}

	protected abstract void sendString(String string) throws IOException;

	protected abstract void waitForResponseToContain(String string, long timeout) throws GsmModemResponseException;

	private void waitForResponseToContain(String string) throws GsmModemResponseException {
		waitForResponseToContain(string, timeout);
	}
}
