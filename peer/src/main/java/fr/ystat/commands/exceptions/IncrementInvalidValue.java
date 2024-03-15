package fr.ystat.commands.exceptions;

public class IncrementInvalidValue extends CommandException{
	public IncrementInvalidValue(int wrongValue){
		super("Value '" + wrongValue + "' is invalid");
	}
}
