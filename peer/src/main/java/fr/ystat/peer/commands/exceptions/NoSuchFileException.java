package fr.ystat.peer.commands.exceptions;

import fr.ystat.commands.exceptions.CommandException;

public class NoSuchFileException extends CommandException {

	public NoSuchFileException(String key) {
		super("File " + key + " does not exist");
	}
}
