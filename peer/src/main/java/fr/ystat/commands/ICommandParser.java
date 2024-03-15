package fr.ystat.commands;

import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.parser.IParser;

@FunctionalInterface
public interface ICommandParser extends IParser<ICommand> {
}
