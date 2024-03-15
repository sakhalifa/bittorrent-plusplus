package fr.ystat.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import lombok.Getter;
import fr.ystat.server.Counter;

@CommandAnnotation(value = "increment", parser = CommandParsers.INCREMENT)
@Getter
public class IncrCounterCommand implements ICommand {
	private final int value;
	public IncrCounterCommand(int value){
		this.value = value;
	}

	@Override
	public String apply(Counter counter) {
		counter.incrementCounter(value);
		return "OK";
	}

	@Override
	public String serialize() {
		return "increment " + value;
	}
}