package fr.romainmoreau.gsmmodem.web;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.romainmoreau.gsmmodem.client.api.GsmModemClient;
import fr.romainmoreau.gsmmodem.client.api.GsmModemException;

@Controller
public class ConsoleController {
	@Autowired
	private GsmModemProperties gsmModemProperties;

	@Autowired
	private GsmModemClient gsmModemClient;

	@MessageMapping("/command")
	public void onCommand(String command) throws IOException, GsmModemException {
		gsmModemClient.sendCommand(command);
	}

	@RequestMapping(value = "/console", method = RequestMethod.GET)
	private String getConsole(Model model) {
		model.addAttribute("portName", gsmModemProperties.getPortName());
		return "console";
	}
}
