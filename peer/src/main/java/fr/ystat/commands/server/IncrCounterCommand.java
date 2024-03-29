package fr.ystat.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.exceptions.IncrementInvalidValue;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import lombok.Getter;
import fr.ystat.server.Counter;

class IncrCounterParser implements ICommandParser {
	@Override
	public ICommand parse(String input) throws ParserException {
		String[] splitted = input.split(" ");
		if (splitted.length < 2) {
			throw new InvalidInputException(input);
		}
		try {
			int val = Integer.parseInt(splitted[1]);
			return new IncrCounterCommand(val);
		} catch (NumberFormatException ex) {
			throw new InvalidInputException(String.format("%s (inside increment parser)", input));
		}
	}
}

@CommandAnnotation(value = "increment", parser = IncrCounterParser.class)
@Getter
public class IncrCounterCommand implements ICommand {
	private final int value;

	public IncrCounterCommand(int value) {
		this.value = value;
	}

	@Override
	public String apply(Counter counter) throws IncrementInvalidValue {
		if (value < 0)
			throw new IncrementInvalidValue(value);
		counter.incrementCounter(value);
		return "OK";
	}

	@Override
	public String serialize() {
		return "increment " + value;
	}
}