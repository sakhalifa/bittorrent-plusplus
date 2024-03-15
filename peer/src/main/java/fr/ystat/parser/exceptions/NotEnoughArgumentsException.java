package fr.ystat.parser.exceptions;

public class NotEnoughArgumentsException extends ParserException{
	public NotEnoughArgumentsException(String input){
		super("Too much arguments");
	}
}
