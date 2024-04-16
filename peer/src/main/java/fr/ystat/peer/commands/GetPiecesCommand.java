package fr.ystat.peer.commands;

import fr.ystat.commands.*;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.parser.ListParser;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.Pair;

import java.util.List;

class GetPiecesParser implements ICommandParser{

	@Override
	public IReceivableCommand parse(String input) throws ParserException {
		String[] splitted = input.split(" ");
		if (splitted.length < 3) {
			throw new InvalidInputException(input);
		}

		String fileHash = ParserUtils.parseKeyCheck(splitted[1]);
		List<Integer> indexList = ParserUtils.parseBufferMap(splitted[2]);

		return new GetPiecesCommand(fileHash, indexList);
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
