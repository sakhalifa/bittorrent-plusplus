package commands.server;

import commands.CommandAnnotation;
import commands.ICommand;
import server.Counter;


@CommandAnnotation("hello")
@SuppressWarnings("unused")
public class HelloCommand implements ICommand {
	@Override
	public String apply(Counter counter) {
		return "hewwo";
	}
}
