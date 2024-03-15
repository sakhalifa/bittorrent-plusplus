package fr.ystat.parser;

import fr.ystat.parser.exceptions.ParserException;

public interface IParser<T> {

	T parse(String input) throws ParserException;
}
