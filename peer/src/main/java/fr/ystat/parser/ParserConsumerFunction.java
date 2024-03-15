package fr.ystat.parser;

import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.Pair;

@FunctionalInterface
public interface ParserConsumerFunction<T> {
	Pair<T, Integer> apply(String input) throws ParserException;
}
