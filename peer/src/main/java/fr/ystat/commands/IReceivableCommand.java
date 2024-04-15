package fr.ystat.commands;

import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.FileInventory;

public interface IReceivableCommand {
	String apply() throws CommandException;
}
