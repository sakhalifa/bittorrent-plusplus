package commands.exceptions;

public class InvalidInputException extends CommandException{
	public InvalidInputException(String input){
		super("Input " + input + " is invalid");
	}
}
