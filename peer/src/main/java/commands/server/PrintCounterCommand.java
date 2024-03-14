package commands.server;

import commands.ICommand;
import server.Counter;

@CommandAnnotation("print")
public class PrintCounterCommand implements ICommand {
	@Override
	public String apply(Counter counter) {
		return "" + counter.getCounter();
	}

	@Override
	public String serialize(){
		return "print";
	}
}
