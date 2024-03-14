package commands.server;

import commands.CommandAnnotation;
import commands.ICommand;
import server.Counter;


@CommandAnnotation("hello")
public class HelloCommand implements ICommand {

	@Override
	public String apply(Counter counter) {
		return "hewwo";
	}

	@Override
	public String serialize(){
		return "hello";
	}
}
