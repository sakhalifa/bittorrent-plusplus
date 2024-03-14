package parser.exceptions;

public class InvalidInputException extends ParserException{
	public InvalidInputException(String input){
		super("Input '" + input + "' is an invalid input");
	}
}
