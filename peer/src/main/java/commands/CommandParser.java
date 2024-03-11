package commands;

import commands.exceptions.CommandException;
import commands.exceptions.InvalidInputException;

public class CommandParser {
	public static ICommand parseInput(String input) throws CommandException {
		input = input.trim();
		if (input.equals("hello")) {
			return new HelloCommand();
		} else if (input.equals("print")) {
			return new PrintCounterCommand();
		} else if (input.startsWith("increment")) {
			String[] splitted = input.split(" ");
			if (splitted.length < 2) {
				throw new InvalidInputException(input);
			}
			try {
				int val = Integer.parseInt(splitted[1]);
				return new IncrCounterCommand(val);
			}catch(NumberFormatException ex){
				throw new InvalidInputException(input);
			}
		}
		throw new InvalidInputException(input);
	}
}
