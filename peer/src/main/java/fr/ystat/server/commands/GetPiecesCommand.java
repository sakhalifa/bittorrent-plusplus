package fr.ystat.server.commands;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.FileInventory;
import fr.ystat.parser.ListParser;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.server.Counter;
import fr.ystat.util.Pair;

import java.util.List;

class GetPiecesParser implements ICommandParser{

	@Override
	public IReceivableCommand parse(String input) throws ParserException {
		String[] splitted = input.split(" ");
		if (splitted.length < 3) {
			throw new InvalidInputException(input);
		}
		if(!input.matches("getpieces [a-z0-9]{32} \\[[0-9]+( [0-9]+)*]")){
			throw new InvalidInputException(input);
		}
		ListParser<Integer> indexListParser = new ListParser<>((integerList, idx) -> {
			try{
				int n = Integer.parseInt(integerList[idx]);
				return new Pair<>(n, 1);
			}catch(NumberFormatException ex){
				throw new InvalidInputException(String.join(" ", integerList));
			}
		});
		List<Integer> indexList = indexListParser.parse(input.substring("getpieces".length() + 32 + 2 + 1, input.length() - 1));
		return new GetPiecesCommand(splitted[1], indexList);
	}
}

@CommandAnnotation(name = "getpieces", parser = GetPiecesParser.class)
public class GetPiecesCommand implements ICommand {
	private final String key;
	private final List<Integer> indexes;

	public GetPiecesCommand(String key, List<Integer> indexes){
		this.key = key;
		this.indexes = indexes;
	}
	@Override
	public String apply() throws CommandException {
		return this.key + " " + this.indexes;
	}
}
