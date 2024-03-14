package commands.server;

import commands.ICommand;
import commands.ICommandParser;
import commands.exceptions.CommandException;
import parser.exceptions.ParserException;

public class ServerCommandParser implements ICommandParser {
	@Override
	public ICommand parse(String input) throws ParserException {
		input = input.trim();
		ICommandParser nextParser = CommandParsers.fromString(input);
		return nextParser.parse(input);
	}
}
