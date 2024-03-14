package commands;

import commands.exceptions.CommandException;
import server.Counter;

import java.io.Serializable;

public interface ICommand extends Serializable {

	String apply(Counter counter) throws CommandException;

	String serialize();


}
