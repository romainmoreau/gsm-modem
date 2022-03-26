package fr.romainmoreau.gsmmodem.web;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

@Validated
@ConfigurationProperties("sms-routing")
public class SmsRoutingProperties {
	@Valid
	private List<SmsRoute> smsRoutes;

	public List<SmsRoute> getSmsRoutes() {
		return smsRoutes;
	}

	public void setSmsRoutes(List<SmsRoute> smsRoutes) {
		this.smsRoutes = smsRoutes;
	}
}
