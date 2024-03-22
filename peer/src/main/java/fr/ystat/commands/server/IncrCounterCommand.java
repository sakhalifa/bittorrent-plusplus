package fr.ystat.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.exceptions.IncrementInvalidValue;
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
	public String apply(Counter counter) throws IncrementInvalidValue {
		if(value < 0)
			throw new IncrementInvalidValue(value);
		counter.incrementCounter(value);
		return "OK";
	}

	@Override
	public String serialize() {
		return "increment " + value;
	}
}