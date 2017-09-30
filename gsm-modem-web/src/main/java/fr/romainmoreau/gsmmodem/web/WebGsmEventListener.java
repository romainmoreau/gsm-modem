package fr.romainmoreau.gsmmodem.web;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.romainmoreau.gsmmodem.client.common.uart.GsmEvent;
import fr.romainmoreau.gsmmodem.client.common.uart.GsmEventListener;
import fr.romainmoreau.gsmmodem.client.common.uart.SmsReceivedEvent;

@Component
public class WebGsmEventListener implements GsmEventListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebGsmEventListener.class);

	@Autowired
	private SmsRouter smsRouter;

	@Override
	public void onGsmEvent(GsmEvent gsmEvent) {
		LOGGER.info("{}", ToStringBuilder.reflectionToString(gsmEvent, ToStringStyle.SHORT_PREFIX_STYLE));
		if (gsmEvent instanceof SmsReceivedEvent) {
			SmsReceivedEvent smsReceivedEvent = (SmsReceivedEvent) gsmEvent;
			smsRouter.route(smsReceivedEvent.getGsmNumber(), smsReceivedEvent.getSms());
		}
	}
}
