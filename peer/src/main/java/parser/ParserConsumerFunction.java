package parser;

import parser.exceptions.ParserException;
import util.Pair;

@FunctionalInterface
public interface ParserConsumerFunction<T> {
	Pair<T, Integer> apply(String input) throws ParserException;
}
