package fr.ystat.peer.commands;

import fr.ystat.commands.*;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;

class InterestedCommandParser implements ICommandParser{
	@Override
	public IReceivableCommand  parse(String input) throws ParserException {
		String[] splitted = input.split(" ");
		if (splitted.length < 2) {
			throw new InvalidInputException(input);
		}
		String fileHash = ParserUtils.parseKeyCheck(splitted[1]);

		return new InterestedCommand(fileHash);
	}
}

@CommandAnnotation(name="interested", parser = InterestedCommandParser.class)
public class InterestedCommand implements ICommand{
	private final String key;

	public InterestedCommand(String key){
		this.key = key;
	}

	@Override
	public String apply() throws CommandException {
		return this.key;
	}

	@Override
	public String serialize() {
		return ICommand.super.serialize() + " " + this.key;
	}
}
