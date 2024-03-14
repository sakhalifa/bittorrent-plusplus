package commands;

import commands.exceptions.CommandException;
import parser.IParser;

@FunctionalInterface
public interface ICommandParser extends IParser<ICommand> {
}
