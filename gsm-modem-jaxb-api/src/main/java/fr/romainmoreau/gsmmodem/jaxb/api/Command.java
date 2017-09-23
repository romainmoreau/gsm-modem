package fr.romainmoreau.gsmmodem.jaxb.api;

import java.io.IOException;

import fr.romainmoreau.gsmmodem.client.api.GsmModemClient;
import fr.romainmoreau.gsmmodem.client.api.GsmModemException;

public interface Command {
	void execute(GsmModemClient gsmModemClient) throws IOException, GsmModemException;
}
