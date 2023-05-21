package fr.romainmoreau.gsmmodem.client.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.springframework.web.client.RestTemplate;

import fr.romainmoreau.gsmmodem.client.api.GsmModemException;
import fr.romainmoreau.gsmmodem.client.common.AbstractGsmModemClient;
import fr.romainmoreau.gsmmodem.jaxb.api.Command;
import fr.romainmoreau.gsmmodem.jaxb.api.Commands;
import fr.romainmoreau.gsmmodem.jaxb.api.SendCommand;
import fr.romainmoreau.gsmmodem.jaxb.api.SendSms;
import fr.romainmoreau.gsmmodem.jaxb.api.SetSMSTextMode;

public class WebGsmModemClient extends AbstractGsmModemClient {
	private final RestTemplate restTemplate;

	private final URI uri;

	public WebGsmModemClient(String protocol, String host, int port) throws MalformedURLException, URISyntaxException {
		restTemplate = new RestTemplate();
		uri = new URI(protocol, null, host, port, "/commands", null, null);
	}

	@Override
	public synchronized void setSMSTextMode() throws IOException, GsmModemException {
		sendCommand(new SetSMSTextMode());
	}

	@Override
	public synchronized void sendSms(String gsmNumber, String sms) throws IOException, GsmModemException {
		SendSms sendSms = new SendSms();
		sendSms.setGsmNumber(gsmNumber);
		sendSms.setSms(sms);
		sendCommand(sendSms);
	}

	@Override
	public void sendCommand(String command) throws IOException, GsmModemException {
		SendCommand sendCommand = new SendCommand();
		sendCommand.setCommand(command);
		sendCommand(sendCommand);
	}

	@Override
	public synchronized void close() throws IOException {
	}

	private void sendCommand(Command command) {
		Commands commands = new Commands();
		commands.setCommands(Arrays.asList(command));
		sendCommands(commands);
	}

	private void sendCommands(Commands commands) {
		restTemplate.postForObject(uri, commands, String.class);
	}
}
