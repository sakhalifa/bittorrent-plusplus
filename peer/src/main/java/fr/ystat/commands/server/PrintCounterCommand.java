package fr.ystat.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.server.Counter;

@CommandAnnotation("print")
@SuppressWarnings("unused")
public class PrintCounterCommand implements ICommand {
	@Override
	public String apply(Counter counter) {
		return "" + counter.getCounter();
	}
}
