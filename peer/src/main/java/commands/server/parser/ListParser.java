package commands.server.parser;

import commands.ICommand;
import commands.ICommandParser;
import commands.exceptions.CommandException;
import lombok.AllArgsConstructor;
import lombok.Value;
import parser.IParser;
import parser.exceptions.ParserException;
import util.Pair;

import java.util.List;
import java.util.function.Function;

public class ListParser<T> implements IParser<List<T>> {

	@Override
	public List<T> parse(String input) throws ParserException {
		return null;
	}

	public ListParser(Function<String, Pair<T, Integer>> consumerFunc){
		// consumerFUnc consumes the list input.
		// As in, imagine having the input "el1 el2 el3 el4" and imagine the consumerFunc taking 2 elements from this list
		// and compacting it into a single object.
		// the given function should take the current input, and return the built object and how many characters it consumed
		// so the list parser can actually shift the input for the next entry in the list
	}
}
