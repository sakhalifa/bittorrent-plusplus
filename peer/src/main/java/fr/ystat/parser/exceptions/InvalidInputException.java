package fr.ystat.parser.exceptions;

public class InvalidInputException extends ParserException{
	public InvalidInputException(String input){
		super("Input '" + input + "' is an invalid input");
	}

	public InvalidInputException(String input, String source){
		super(String.format("[%s] Input %s is an invalid input!", source, input));
	}
}
