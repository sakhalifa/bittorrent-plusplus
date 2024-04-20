package fr.ystat.peer.commands;

import fr.ystat.commands.*;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.parser.ParserUtils;
import fr.ystat.parser.exceptions.ParserException;

import java.util.List;

class HaveParser implements ICommandParser {

    @Override
    public IReceivableCommand parse(String input) throws ParserException {

        String[] splitted = ParserUtils.expectArgs(input, 3, "have");

        String fileHash = ParserUtils.parseKeyCheck(splitted[1]);
        List<Integer> indexList = ParserUtils.parseBufferMap(
                input.substring(splitted[0].length() + splitted[1].length() + 2),
                "have"
        );

        return new HaveCommand(fileHash, indexList);
    }
}


@CommandAnnotation(name = "have", parser = HaveParser.class)
public class HaveCommand implements ICommand {

    private final String key;
    private final List<Integer> indexes;

    public HaveCommand(String key, List<Integer> indexes){
        this.key = key;
        this.indexes = indexes;
    }

    @Override
    public String apply() throws CommandException {
        return "";
    }
}
