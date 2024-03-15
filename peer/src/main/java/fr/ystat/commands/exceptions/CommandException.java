package fr.ystat.commands.exceptions;

public abstract class CommandException extends Exception{
	public CommandException(String message) {
		super(message);
	}
}
