package fr.ystat.peer.commands;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.parser.ListParser;
import fr.ystat.parser.ParserUtils;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


class DataParser implements ICommandParser {

    @Override
    public IReceivableCommand parse(String input) throws ParserException {

        String[] splitted = ParserUtils.expectArgs(input, 3, "data");

        String fileHash = ParserUtils.parseKeyCheck(splitted[1]);

        // Parse Map of id:data :/
        List<Pair<Integer, String>> dataList = new ListParser<>((lst, idx) -> {
            String[] split = lst[idx].split(":");
            if (split.length != 2) throw new InvalidInputException(input, "data.badElFormat." + idx);
            int buffer_index = Integer.parseInt(split[0]);
            String index_data = split[1];
            return new Pair<>(new Pair<>(buffer_index, index_data), 1);
        }).parse(input.substring(splitted[0].length() + 1 + fileHash.length() + 1 + 1, input.length() - 1));
        return new DataCommand(dataList);
    }
}


@CommandAnnotation(name = "data", parser = DataParser.class)
public class DataCommand implements IReceivableCommand {
    private final Map<Integer, String> dataList;

    public DataCommand(List<Pair<Integer, String>> dataList) throws IllegalArgumentException {
        // Throws if duplicates keys are present
        this.dataList = new HashMap<>(dataList.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
    }
    @Override
    public String apply() throws CommandException {


        return "";  // You don't want to send it, don't you ? :3
    }
}
