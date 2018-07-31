package fr.romainmoreau.gsmmodem.client.jserialcomm;

import static com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
import static com.fazecast.jSerialComm.SerialPort.NO_PARITY;
import static com.fazecast.jSerialComm.SerialPort.ONE_STOP_BIT;

import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import fr.romainmoreau.gsmmodem.client.common.uart.AbstractUARTGsmModemClient;

public class JSerialCommGsmModemClient extends AbstractUARTGsmModemClient implements SerialPortDataListener {
	private final SerialPort serialPort;

	private SerialEventExceptionListener serialEventExceptionListener;

	public JSerialCommGsmModemClient(String portName, long timeout) throws IOException {
		super(timeout);
		serialPort = SerialPort.getCommPort(portName);
		serialPort.setBaudRate(115200);
		serialPort.setNumDataBits(8);
		serialPort.setNumStopBits(ONE_STOP_BIT);
		serialPort.setParity(NO_PARITY);
		serialPort.addDataListener(this);
		if (!serialPort.openPort()) {
			throw new IOException("Port not opened");
		}
	}

	@Override
	public int getListeningEvents() {
		return LISTENING_EVENT_DATA_AVAILABLE;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		try {
			byte[] readBytes = readBytes();
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

	@Override
	public synchronized void close() throws IOException {
		if (!serialPort.closePort()) {
			throw new IOException("Port not closed");
		}
	}

	@Override
	protected void sendString(String string) throws IOException {
		byte[] bytes = string.getBytes();
		int bytesWritten = serialPort.writeBytes(bytes, bytes.length);
		if (bytesWritten != bytes.length) {
			throw new IOException("Error writing bytes");
		}
	}

	private byte[] readBytes() {
		int bytesAvailable = serialPort.bytesAvailable();
		if (bytesAvailable == 0) {
			return null;
		} else if (bytesAvailable > 0) {
			byte[] bytes = new byte[bytesAvailable];
			if (serialPort.readBytes(bytes, bytes.length) == bytes.length) {
				return bytes;
			} else {
				throw new RuntimeException("Error reading bytes");
			}
		} else {
			throw new RuntimeException("Port not opened");
		}
	}

	public void setSerialEventExceptionListener(SerialEventExceptionListener serialEventExceptionListener) {
		this.serialEventExceptionListener = serialEventExceptionListener;
	}
}
