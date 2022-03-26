package fr.romainmoreau.gsmmodem.jaxb.api;

import java.io.IOException;

import fr.romainmoreau.gsmmodem.client.api.GsmModemClient;
import fr.romainmoreau.gsmmodem.client.api.GsmModemException;
import jakarta.xml.bind.annotation.XmlValue;

public class SendCommand implements Command {
	private String command;

	@Override
	public void execute(GsmModemClient gsmModemClient) throws IOException, GsmModemException {
		gsmModemClient.sendCommand(command);
	}

	@XmlValue
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
}
