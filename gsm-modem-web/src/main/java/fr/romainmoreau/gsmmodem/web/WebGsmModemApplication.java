package fr.romainmoreau.gsmmodem.web;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import fr.romainmoreau.gsmmodem.client.api.GsmModemClient;
import fr.romainmoreau.gsmmodem.client.jssc.JsscGsmModemClient;

@EnableAsync
@SpringBootApplication(scanBasePackages = "fr.romainmoreau.gsmmodem.web")
public class WebGsmModemApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebGsmModemApplication.class);

	@Autowired
	private GsmModemProperties gsmModemProperties;

	@Autowired
	private WebGsmEventListener webGsmEventListener;

	@Bean
	public GsmModemClient gsmModemClient() throws IOException {
		LOGGER.info("Creating JSSC gsm-modem client using port name {} and timeout {}",
				gsmModemProperties.getPortName(), gsmModemProperties.getTimeout());
		JsscGsmModemClient jsscGsmModemClient = new JsscGsmModemClient(gsmModemProperties.getPortName(),
				gsmModemProperties.getTimeout());
		jsscGsmModemClient.setGsmEventListener(webGsmEventListener);
		jsscGsmModemClient.setReadLineListener(readLine -> LOGGER.info("{}", readLine));
		jsscGsmModemClient.setSerialEventExceptionListener(e -> LOGGER.error("Exception", e));
		return jsscGsmModemClient;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static final void main(String[] args) throws Exception {
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> LOGGER.error("Uncaught exception thrown", e));
		SpringApplication.run(WebGsmModemApplication.class, args);
	}
}
