package fr.ystat.peer.commands;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.IReceivableCommand;
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
		if(splitted[1].length() != 32){
			throw new InvalidInputException(input);
		}
		System.out.println(splitted[1]);
		System.out.println(splitted[1].matches("[a-z0-9]*"));
		if(!splitted[1].matches("[a-z0-9]*")){
			throw new InvalidInputException(input);
		}
		return new InterestedCommand(splitted[1]);
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
