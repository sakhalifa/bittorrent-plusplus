package commands.server;

import commands.ICommand;
import server.Counter;

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
