package fr.romainmoreau.gsmmodem.client.common.uart;

public class SmsSentEvent extends GsmEvent {
	private final int mr;

	public SmsSentEvent(int mr) {
		this.mr = mr;
	}

	public int getMr() {
		return mr;
	}
}
