package commands.server;

import commands.ICommand;
import commands.ICommandParser;
import commands.exceptions.CommandException;
import parser.exceptions.InvalidInputException;
import parser.exceptions.ParserException;

import java.util.Arrays;

public enum CommandParsers implements ICommandParser {
	//<editor-fold desc="HELLO" defaultstate="collapsed">
	HELLO {
		@Override
		public ICommand parse(String input) throws ParserException {
			return new HelloCommand();
		}
	},
	//</editor-fold>
	//<editor-fold desc="PRINT" defaultstate="collapsed">
	PRINT {
		@Override
		public ICommand parse(String input) throws ParserException {
			return new PrintCounterCommand();
		}
	},
	//</editor-fold>
	//<editor-fold desc="INCREMENT" defaultstate="collapsed">
	INCREMENT {
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
				throw new InvalidInputException(input);
			}
		}
	}
	//</editor-fold>
	;

	public static CommandParsers fromString(String input) throws ParserException {
		return Arrays.stream(CommandParsers.values())
				.filter((v) -> v.name().toLowerCase().equals(input.split(" ")[0]))
				.findFirst()
				.orElseThrow(() -> new InvalidInputException("Invalid input"));
//		if (input.equals("hello")) {
//			return Commands.HELLO;
//		} else if (input.equals("print")) {
//			return Commands.PRINT.parse(input.substring("print".length()));
//		} else if (input.startsWith("increment")) {
//			return Commands.INCREMENT.parse(input.substring("increment".length() + 1));
//		}
	}
}
