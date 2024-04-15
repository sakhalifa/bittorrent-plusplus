package fr.ystat.tracker.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.ISendableCommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.server.Counter;

@CommandAnnotation(name = "getfile")
public class GetFileCommand implements ISendableCommand {

	private final String key;

	public GetFileCommand(String key) {
		this.key = key;
	}

	@Override
	public String serialize() {
		return String.format("getfile %s", key);
	}
}
