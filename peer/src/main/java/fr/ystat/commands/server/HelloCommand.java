package fr.ystat.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.server.Counter;


@CommandAnnotation("hello")
@SuppressWarnings("unused")
public class HelloCommand implements ICommand {
	@Override
	public String apply(Counter counter) {
		return "hewwo";
	}
}
