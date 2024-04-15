package fr.ystat.commands;

import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.FileInventory;
import fr.ystat.server.Counter;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public interface ICommand extends IReceivableCommand, ISendableCommand{
}
