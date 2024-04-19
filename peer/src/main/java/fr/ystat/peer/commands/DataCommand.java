package fr.ystat.peer.commands;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.parser.ParserUtils;
import fr.ystat.parser.exceptions.ParserException;


class DataParser implements ICommandParser {

    @Override
    public IReceivableCommand parse(String input) throws ParserException {

        String[] splitted = ParserUtils.expectArgs(input, 3, "data");

        String fileHash = ParserUtils.parseKeyCheck(splitted[1]);

        // Parse Map of id:data :/

        return new DataCommand();
    }
}



@CommandAnnotation(name = "data", parser = DataParser.class)
public class DataCommand implements IReceivableCommand {
    @Override
    public String apply() throws CommandException {


        return "";  // You don't want to send it, don't you ? :3
    }
}
