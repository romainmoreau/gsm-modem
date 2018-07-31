package fr.romainmoreau.gsmmodem.jaxb.api;

import java.io.IOException;

import fr.romainmoreau.gsmmodem.client.api.GsmModemClient;
import fr.romainmoreau.gsmmodem.client.api.GsmModemException;

public class SetSMSTextMode implements Command {
	@Override
	public void execute(GsmModemClient gsmModemClient) throws IOException, GsmModemException {
		gsmModemClient.setSMSTextMode();
	}
}
