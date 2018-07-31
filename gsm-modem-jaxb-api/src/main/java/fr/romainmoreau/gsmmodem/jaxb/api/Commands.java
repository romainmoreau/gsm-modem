package fr.romainmoreau.gsmmodem.jaxb.api;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Commands {
	private List<Command> commands;

	@XmlElements({ @XmlElement(name = "sendSms", type = SendSms.class),
			@XmlElement(name = "setSMSTextMode", type = SetSMSTextMode.class) })
	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}
}
