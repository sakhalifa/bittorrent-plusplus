package fr.ystat.tracker.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ISendableCommand;

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
