package fr.ystat.parser.exceptions;

public class UnknownCommandException extends ParserException{
	public UnknownCommandException(String command) {
		super("Unknown command '" + command + "'");
	}
}
