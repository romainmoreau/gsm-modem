package fr.romainmoreau.gsmmodem.client.jssc;

import java.io.IOException;

import fr.romainmoreau.gsmmodem.client.common.uart.AbstractUARTGsmModemClient;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;

public class JsscGsmModemClient extends AbstractUARTGsmModemClient {
	private final SerialPort serialPort;

	private SerialEventExceptionListener serialEventExceptionListener;

	public JsscGsmModemClient(String portName, long timeout) throws IOException {
		super(timeout);
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
			serialPort.writeString(string);
		} catch (SerialPortException e) {
			throw new IOException(e);
		}
	}

	private void serialEvent(SerialPortEvent serialPortEvent) {
		try {
			byte[] readBytes = serialPort.readBytes();
			if (readBytes != null) {
				for (byte readByte : readBytes) {
					onReadByte(readByte);
				}
			}
		} catch (Exception e) {
			if (serialEventExceptionListener != null) {
				serialEventExceptionListener.onException(e);
			}
		}
	}

	public void setSerialEventExceptionListener(SerialEventExceptionListener serialEventExceptionListener) {
		this.serialEventExceptionListener = serialEventExceptionListener;
	}
}
