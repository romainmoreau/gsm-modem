package fr.romainmoreau.gsmmodem.client.jssc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.romainmoreau.gsmmodem.client.api.GsmModemResponseException;
import fr.romainmoreau.gsmmodem.client.common.uart.AbstractUARTGsmModemClient;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;

public class JsscGsmModemClient extends AbstractUARTGsmModemClient {
	private final SerialPort serialPort;

	private final List<Byte> readByteList;

	private volatile String response;

	public JsscGsmModemClient(String portName, long timeout) throws IOException {
		super(timeout);
		readByteList = new ArrayList<>();
		try {
			serialPort = new SerialPort(portName);
			serialPort.openPort();
			serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE, false, false);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
			serialPort.addEventListener(this::serialEvent, SerialPort.MASK_RXCHAR);
		} catch (SerialPortException e) {
			throw new IOException(e);
		}
	}

	@Override
	public synchronized void close() throws IOException {
		try {
			serialPort.closePort();
		} catch (SerialPortException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected void sendString(String string) throws IOException {
		try {
			response = null;
			serialPort.writeString(string);
		} catch (SerialPortException e) {
			throw new IOException(e);
		}
	}

	@Override
	protected void waitForResponseToContain(String string, long timeout) throws GsmModemResponseException {
		try {
			do {
				wait(timeout);
			} while (response == null || !response.contains(string));
			if (response == null || !response.contains(string)) {
				throw new GsmModemResponseException(response);
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	private void serialEvent(SerialPortEvent serialPortEvent) {
		try {
			if (response == null && !readByteList.isEmpty()) {
				readByteList.clear();
			}
			byte[] readBytes = serialPort.readBytes();
			if (readBytes != null) {
				for (byte readByte : readBytes) {
					readByteList.add(readByte);
				}
				readBytes = new byte[readByteList.size()];
				for (int i = 0; i < readByteList.size(); i++) {
					readBytes[i] = readByteList.get(i);
				}
				response = new String(readBytes);
				synchronized (JsscGsmModemClient.this) {
					JsscGsmModemClient.this.notify();
				}
			}
		} catch (SerialPortException e) {
			throw new RuntimeException(e);
		}
	}
}
