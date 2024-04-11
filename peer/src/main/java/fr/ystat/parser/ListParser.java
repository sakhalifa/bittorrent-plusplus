package fr.ystat.parser;

import fr.ystat.commands.ICommand;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.exceptions.CommandException;
import lombok.AllArgsConstructor;
import lombok.Value;
import fr.ystat.parser.IParser;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListParser<T> implements IParser<List<T>> {

	private final ParserConsumerFunction<T> consumerFunction;

	@Override
	public List<T> parse(String input) throws ParserException {
		var splitted = input.split(" ");
		List<T> toReturn = new ArrayList<>();
		int idx = 0;
		while(idx < splitted.length){
			Pair<T, Integer> result = this.consumerFunction.apply(splitted, idx);
			toReturn.add(result.getFirst());
			idx += result.getSecond();
		}
		return toReturn;
	}

	public ListParser(ParserConsumerFunction<T> consumerFunc){
		// consumerFUnc consumes the list input.
		// As in, imagine having the input "el1 el2 el3 el4" and imagine the consumerFunc taking 2 elements from this list
		// and compacting it into a single object.
		// the given function should take the current input, and return the built object and how many characters it consumed
		// so the list parser can actually shift the input for the next entry in the list
		this.consumerFunction = consumerFunc;
	}
}
