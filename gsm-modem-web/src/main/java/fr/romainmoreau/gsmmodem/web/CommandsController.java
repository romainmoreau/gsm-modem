package fr.romainmoreau.gsmmodem.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.romainmoreau.gsmmodem.jaxb.api.Commands;

@Controller
@RequestMapping("/commands")
public class CommandsController {
	@Autowired
	private GsmModemWorker gsmModemWorker;

	@PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
	public @ResponseBody boolean post(@RequestBody Commands commands) {
		return gsmModemWorker.offer(commands);
	}
}
