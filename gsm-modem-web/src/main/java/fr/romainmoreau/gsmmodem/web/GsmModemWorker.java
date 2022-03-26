package fr.romainmoreau.gsmmodem.web;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.romainmoreau.gsmmodem.client.api.GsmModemClient;
import fr.romainmoreau.gsmmodem.jaxb.api.Command;
import fr.romainmoreau.gsmmodem.jaxb.api.Commands;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class GsmModemWorker {
	private static final Logger LOGGER = LoggerFactory.getLogger(GsmModemWorker.class);

	@Autowired
	private GsmModemClient gsmModemClient;

	private final Thread thread;

	private final BlockingQueue<Commands> commandsQueue;

	private volatile boolean stop;

	public GsmModemWorker() {
		thread = new Thread(this::run, "gsm-modem");
		commandsQueue = new LinkedBlockingQueue<>();
	}

	@PostConstruct
	private void postConstruct() {
		thread.start();
	}

	@PreDestroy
	private void preDestroy() {
		commandsQueue.clear();
		stop = true;
		thread.interrupt();
	}

	public boolean offer(final Commands commands) {
		return commandsQueue.offer(commands);
	}

	private void run() {
		LOGGER.info("gsm-modem thread started");
		do {
			try {
				Commands commands = commandsQueue.take();
				if (commands != null && commands.getCommands() != null) {
					for (Command command : commands.getCommands()) {
						try {
							command.execute(gsmModemClient);
						} catch (Exception e) {
							LOGGER.error("Exception while executing command", e);
						}
					}
				}
			} catch (InterruptedException e) {
				stop = true;
			}
		} while (!stop);
		LOGGER.info("gsm-modem thread stopped");
	}
}
