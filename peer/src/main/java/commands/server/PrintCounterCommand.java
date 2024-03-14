package commands.server;

import commands.CommandAnnotation;
import commands.ICommand;
import server.Counter;

@CommandAnnotation("print")
@SuppressWarnings("unused")
public class PrintCounterCommand implements ICommand {
	@Override
	public String apply(Counter counter) {
		return "" + counter.getCounter();
	}
}
