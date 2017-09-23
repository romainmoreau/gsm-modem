package fr.romainmoreau.gsmmodem.client.web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import org.springframework.web.client.RestTemplate;

import fr.romainmoreau.gsmmodem.client.api.GsmModemException;
import fr.romainmoreau.gsmmodem.client.common.AbstractGsmModemClient;
import fr.romainmoreau.gsmmodem.jaxb.api.Command;
import fr.romainmoreau.gsmmodem.jaxb.api.Commands;
import fr.romainmoreau.gsmmodem.jaxb.api.SendSms;

public class WebGsmModemClient extends AbstractGsmModemClient {
	private final RestTemplate restTemplate;

	private final URI uri;

	public WebGsmModemClient(String protocol, String host, int port) throws MalformedURLException, URISyntaxException {
		restTemplate = new RestTemplate();
		uri = new URL(protocol, host, port, "/").toURI();
	}

	@Override
	public synchronized void sendSms(String gsmNumber, String sms) throws IOException, GsmModemException {
		SendSms sendSms = new SendSms();
		sendSms.setGsmNumber(gsmNumber);
		sendSms.setSms(sms);
		sendCommand(sendSms);
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
