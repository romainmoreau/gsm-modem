package fr.romainmoreau.gsmmodem.web;

import org.springframework.util.StringUtils;

import jakarta.validation.constraints.NotNull;

public class SmsRoute {
	@NotNull
	private String prefix;

	@NotNull
	private String endpointUrl;

	public boolean matches(String sms) {
		return StringUtils.startsWithIgnoreCase(sms, prefix);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getEndpointUrl() {
		return endpointUrl;
	}

	public void setEndpointUrl(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}
}
