package fr.romainmoreau.gsmmodem.jaxb.api;

import java.io.IOException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import fr.romainmoreau.gsmmodem.client.api.GsmModemClient;
import fr.romainmoreau.gsmmodem.client.api.GsmModemException;

public class SendSms implements Command {
	private String gsmNumber;

	private String sms;

	@Override
	public void execute(GsmModemClient gsmModemClient) throws IOException, GsmModemException {
		gsmModemClient.sendSms(gsmNumber, sms);
	}

	@XmlAttribute(required = true)
	public String getGsmNumber() {
		return gsmNumber;
	}

	public void setGsmNumber(String gsmNumber) {
		this.gsmNumber = gsmNumber;
	}

	@XmlValue
	public String getSms() {
		return sms;
	}

	public void setSms(String sms) {
		this.sms = sms;
	}
}
