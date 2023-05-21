package fr.romainmoreau.gsmmodem.web;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import fr.romainmoreau.gsmmodem.client.api.GsmModemClient;
import fr.romainmoreau.gsmmodem.client.api.GsmModemException;
import fr.romainmoreau.gsmmodem.client.jserialcomm.JSerialCommGsmModemClient;

@EnableAsync
@EnableWebSocketMessageBroker
@SpringBootApplication(scanBasePackages = "fr.romainmoreau.gsmmodem.web")
@ConfigurationPropertiesScan
public class WebGsmModemApplication implements WebSocketMessageBrokerConfigurer {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebGsmModemApplication.class);

	@Autowired
	private GsmModemProperties gsmModemProperties;

	@Autowired
	private WebGsmEventListener webGsmEventListener;

	@Bean
	GsmModemClient gsmModemClient(SimpMessagingTemplate simpMessagingTemplate) throws IOException, GsmModemException {
		LOGGER.info("Creating jSerialComm gsm-modem client using port name {} and timeout {}",
				gsmModemProperties.getPortName(), gsmModemProperties.getTimeout());
		JSerialCommGsmModemClient jSerialCommGsmModemClient = new JSerialCommGsmModemClient(
				gsmModemProperties.getPortName(), gsmModemProperties.getTimeout());
		jSerialCommGsmModemClient.setGsmEventListener(webGsmEventListener);
		jSerialCommGsmModemClient.setReadLineListener(readLine -> {
			LOGGER.info("{}", readLine);
			simpMessagingTemplate.convertAndSend("/topic/line", readLine);
		});
		jSerialCommGsmModemClient.setSerialEventExceptionListener(e -> LOGGER.error("Exception", e));
		jSerialCommGsmModemClient.setSMSTextMode();
		return jSerialCommGsmModemClient;
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
		messageBrokerRegistry.enableSimpleBroker("/topic");
		messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stompEndpoint").withSockJS();
	}

	public static final void main(String[] args) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> LOGGER.error("Uncaught exception thrown", e));
		SpringApplication.run(WebGsmModemApplication.class, args);
	}
}
