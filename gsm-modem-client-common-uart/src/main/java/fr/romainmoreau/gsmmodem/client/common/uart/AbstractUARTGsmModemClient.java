package fr.romainmoreau.gsmmodem.client.common.uart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import fr.romainmoreau.gsmmodem.client.api.GsmModemException;
import fr.romainmoreau.gsmmodem.client.api.GsmModemResponseException;
import fr.romainmoreau.gsmmodem.client.common.AbstractGsmModemClient;

public abstract class AbstractUARTGsmModemClient extends AbstractGsmModemClient {
	protected static final byte LF = '\n';
	protected static final char CR = '\r';
	protected static final String SUB = "\u001a";
	protected static final char SPACE = ' ';
	protected static final byte K = 'K';
	protected static final String OK = "OK";
	protected static final String ERROR = "ERROR";
	protected static final String PLUS_CMS_ERROR_COLON = "+CMS ERROR:";
	protected static final String AT_PLUS = "AT+";
	protected static final String PLUS = "+";
	protected static final String PROMPT = "> ";
	protected static final String PLUS_CMT_COLON = "+CMT: ";
	protected static final String DOUBLE_QUOTE = "\"";
	protected static final String PLUS_CMGS_COLON = "+CMGS: ";

	private final long timeout;

	private final ByteArrayOutputStream byteArrayOutputStream;

	private final BlockingQueue<GsmEvent> gsmEventBlockingQueue;

	private String receivedSmsFirstLine;

	private GsmEventListener gsmEventListener;

	private ReadByteListener readByteListener;

	private ReadLineListener readLineListener;

	public AbstractUARTGsmModemClient(long timeout) {
		this.timeout = timeout;
		this.byteArrayOutputStream = new ByteArrayOutputStream();
		this.gsmEventBlockingQueue = new LinkedBlockingDeque<>();
	}

	@Override
	public synchronized void sendSms(String gsmNumber, String sms) throws IOException, GsmModemException {
		sendString("AT+CMGF=1" + CR);
		waitForFirstOkEventOrErrorEvent();
		sendString("AT+CMGS=\"" + gsmNumber + "\"" + CR);
		waitForFirstPromptEvent();
		sendString(sms + SUB);
		waitForFirstOkEventOrErrorEvent();
	}

	private GsmEvent waitForGsmEvent() throws GsmModemResponseException {
		GsmEvent gsmEvent = null;
		try {
			gsmEvent = gsmEventBlockingQueue.poll(timeout, TimeUnit.MILLISECONDS);
			if (gsmEvent == null) {
				throw new GsmModemResponseException(null);
			}
			return gsmEvent;
		} catch (InterruptedException e) {
			throw new GsmModemResponseException(null, e);
		}
	}

	private GsmEvent waitForFirstGsmEvent(Predicate<GsmEvent> predicate) throws GsmModemResponseException {
		GsmEvent gsmEvent;
		do {
			gsmEvent = waitForGsmEvent();
		} while (!predicate.test(gsmEvent));
		return gsmEvent;
	}

	private OkEvent waitForFirstOkEventOrErrorEvent() throws GsmModemResponseException {
		GsmEvent gsmEvent = waitForFirstGsmEvent(e -> e instanceof OkEvent || e instanceof ErrorEvent);
		if (gsmEvent instanceof ErrorEvent) {
			throw new GsmModemResponseException(((ErrorEvent) gsmEvent).getError());
		}
		return (OkEvent) gsmEvent;
	}

	private PromptEvent waitForFirstPromptEvent() throws GsmModemResponseException {
		return (PromptEvent) waitForFirstGsmEvent(gsmEvent -> gsmEvent instanceof PromptEvent);
	}

	public long getTimeout() {
		return timeout;
	}

	private void onReadLine(String readLine) {
		if (readLineListener != null) {
			readLineListener.onReadLine(readLine);
		}
		if (receivedSmsFirstLine != null) {
			try {
				onGsmEvent(new SmsReceivedEvent(receivedSmsFirstLine.split(DOUBLE_QUOTE)[1], readLine));
			} finally {
				receivedSmsFirstLine = null;
			}
		}
		if (readLine.startsWith(PLUS_CMT_COLON)) {
			receivedSmsFirstLine = readLine;
		}
		if (readLine.equals(OK)) {
			onGsmEvent(new OkEvent());
		}
		if (readLine.equals(ERROR)) {
			onGsmEvent(new ErrorEvent(null));
		}
		if (readLine.startsWith(PLUS_CMS_ERROR_COLON)) {
			onGsmEvent(new ErrorEvent(readLine.substring(PLUS_CMS_ERROR_COLON.length())));
		}
		if (readLine.startsWith(PLUS_CMGS_COLON)) {
			onGsmEvent(new SmsSentEvent(Integer.parseInt(readLine.substring(PLUS_CMGS_COLON.length()))));
		}
	}

	protected abstract void sendString(String string) throws IOException;

	private String getReadLine() {
		try {
			return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private Byte lastReadByte;

	protected void onReadByte(byte readByte) {
		if (readByteListener != null) {
			readByteListener.onReadByte(readByte);
		}
		if (readByte == LF || readByte == CR) {
			if (lastReadByte != null && lastReadByte != LF && lastReadByte != CR) {
				onReadLine(getReadLine());
				byteArrayOutputStream.reset();
			}
		} else {
			byteArrayOutputStream.write(readByte);
		}
		if (readByte == SPACE && byteArrayOutputStream.size() == 2 && getReadLine().equals(PROMPT)) {
			onGsmEvent(new PromptEvent());
		}
		lastReadByte = readByte;
	}

	private void onGsmEvent(GsmEvent gsmEvent) {
		gsmEventBlockingQueue.add(gsmEvent);
		if (gsmEventListener != null) {
			gsmEventListener.onGsmEvent(gsmEvent);
		}
	}

	public void setGsmEventListener(GsmEventListener gsmEventListener) {
		this.gsmEventListener = gsmEventListener;
	}

	public void setReadByteListener(ReadByteListener readByteListener) {
		this.readByteListener = readByteListener;
	}

	public void setReadLineListener(ReadLineListener readLineListener) {
		this.readLineListener = readLineListener;
	}
}
