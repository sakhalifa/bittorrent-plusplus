package fr.ystat.commands;

import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.server.Counter;

import java.io.Serializable;

public interface ICommand {

	String apply(Counter counter) throws CommandException;

	default String serialize(){
		return getClass().getAnnotation(CommandAnnotation.class).name();
	}
}
