package fr.ystat.peer.commands.exceptions;

import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.StockedFile;

public class InvalidPartition extends CommandException {
	public InvalidPartition(StockedFile file, int partition) {
		super("File '" + file.getProperties().getHash() + "' does not have partition n°" + partition);
	}
}
