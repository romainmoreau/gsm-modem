package fr.romainmoreau.gsmmodem.web;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SmsRouter {
	private static final Logger LOGGER = LoggerFactory.getLogger(SmsRouter.class);

	@Autowired
	private SmsRoutingProperties smsRoutingProperties;

	@Autowired
	private RestTemplate restTemplate;

	@Async
	public void route(String gsmNumber, String sms) {
		Optional.ofNullable(smsRoutingProperties.getSmsRoutes()).ifPresent(smsRoutes -> smsRoutes.stream()
				.filter(smsRoute -> smsRoute.matches(sms)).findFirst().ifPresent(smsRoute -> {
					URI uri = UriComponentsBuilder.fromHttpUrl(smsRoute.getEndpointUrl())
							.queryParam("gsmNumber", "{gsmNumber}").queryParam("sms", "{sms}").build(gsmNumber, sms);
					LOGGER.info("Routing SMS to {}", uri);
					restTemplate.postForObject(uri, null, String.class);
				}));
	}
}
