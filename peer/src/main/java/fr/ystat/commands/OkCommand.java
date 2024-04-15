package fr.ystat.commands;

import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.FileInventory;
import fr.ystat.server.Counter;

@CommandAnnotation(name = "ok")
public class OkCommand implements ICommand{
	@Override
	public String apply() throws CommandException {
		throw new UnsupportedOperationException("Cannot use apply on 'ok' command!");
	}

}
