package fr.romainmoreau.gsmmodem.client.common.uart;

public class SmsReceivedEvent extends GsmEvent {
	private final String gsmNumber;

	private final String sms;

	public SmsReceivedEvent(String gsmNumber, String sms) {
		this.gsmNumber = gsmNumber;
		this.sms = sms;
	}

	public String getGsmNumber() {
		return gsmNumber;
	}

	public String getSms() {
		return sms;
	}
}
