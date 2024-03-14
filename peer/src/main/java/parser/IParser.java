package parser;

import parser.exceptions.ParserException;

public interface IParser<T> {

	T parse(String input) throws ParserException;
}
