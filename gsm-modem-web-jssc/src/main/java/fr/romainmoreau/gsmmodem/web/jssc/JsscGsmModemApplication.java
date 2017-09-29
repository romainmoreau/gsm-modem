package fr.romainmoreau.gsmmodem.web.jssc;

import java.io.IOException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import fr.romainmoreau.gsmmodem.client.api.GsmModemClient;
import fr.romainmoreau.gsmmodem.client.jssc.JsscGsmModemClient;

@SpringBootApplication(scanBasePackages = "fr.romainmoreau.gsmmodem.web")
public class JsscGsmModemApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsscGsmModemApplication.class);

	@Autowired
	private JsscGsmModemProperties gsmModemProperties;

	@Bean
	public GsmModemClient gsmModemClient() throws IOException {
		LOGGER.info("Creating JSSC gsm-modem client using port name {} and timeout {}",
				gsmModemProperties.getPortName(), gsmModemProperties.getTimeout());
		JsscGsmModemClient jsscGsmModemClient = new JsscGsmModemClient(gsmModemProperties.getPortName(),
				gsmModemProperties.getTimeout());
		jsscGsmModemClient.setGsmEventListener(gsmEvent -> LOGGER.info("{}",
				ToStringBuilder.reflectionToString(gsmEvent, ToStringStyle.SHORT_PREFIX_STYLE)));
		jsscGsmModemClient.setReadLineListener(readLine -> LOGGER.info("{}", readLine));
		return jsscGsmModemClient;
	}

	public static final void main(String[] args) throws Exception {
		SpringApplication.run(JsscGsmModemApplication.class, args);
	}
}
