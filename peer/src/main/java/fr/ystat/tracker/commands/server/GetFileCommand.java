package fr.ystat.tracker.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.server.Counter;

@CommandAnnotation(name = "getfile")
public class GetFileCommand implements ICommand {

	private final String key;

	public GetFileCommand(String key) {
		this.key = key;
	}

	@Override
	public String apply(Counter counter) throws CommandException {
		throw new UnsupportedOperationException("Cannot use apply on getfile command");
	}

	@Override
	public String serialize() {
		return String.format("getfile %s", key);
	}
}
