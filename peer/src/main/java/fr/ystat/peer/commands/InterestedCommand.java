package fr.ystat.peer.commands;

import fr.ystat.commands.*;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.*;
import fr.ystat.parser.ParserUtils;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.peer.commands.exceptions.NoSuchFileException;

class InterestedCommandParser implements ICommandParser {
	@Override
	public IReceivableCommand parse(String input) throws ParserException {
		String[] splitted = input.split(" ");
		if (splitted.length < 2) {
			throw new InvalidInputException(input);
		}
		String fileHash = ParserUtils.parseKeyCheck(splitted[1]);

		return new InterestedCommand(fileHash);
	}
}

@CommandAnnotation(name = "interested", parser = InterestedCommandParser.class)
public class InterestedCommand implements ICommand {
	private final String key;

	public InterestedCommand(String key) {
		this.key = key;
	}

	@Override
	public String apply() throws CommandException {
		StockedFile file = FileInventory.getInstance().getStockedFile(key);
		if (file == null) {
			throw new NoSuchFileException(key);
		}
		return new HaveCommand(key, file.getBitSet()).serialize();
	}

	@Override
	public String serialize() {
		return ICommand.super.serialize() + " " + this.key;
	}
}
