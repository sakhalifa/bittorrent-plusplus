package fr.ystat.commands;

import fr.ystat.commands.exceptions.CommandException;

@CommandAnnotation(name = "ok")
public class OkCommand implements ICommand{
	@Override
	public String apply() throws CommandException {
		throw new UnsupportedOperationException("Cannot use apply on 'ok' command!");
	}

}
