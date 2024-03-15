package fr.ystat.commands;

import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.server.Counter;

import java.io.Serializable;

public interface ICommand extends Serializable {

	String apply(Counter counter) throws CommandException;

	@SuppressWarnings("unused")
	default String serialize(){
		return getClass().getAnnotation(CommandAnnotation.class).value();
	}
}
