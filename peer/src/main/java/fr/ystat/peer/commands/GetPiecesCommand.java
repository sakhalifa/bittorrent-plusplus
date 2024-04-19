package fr.ystat.peer.commands;

import fr.ystat.commands.*;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.parser.ParserUtils;
import fr.ystat.parser.exceptions.ParserException;

import java.util.List;

class GetPiecesParser implements ICommandParser{

	@Override
	public IReceivableCommand parse(String input) throws ParserException {
		String[] splitted = ParserUtils.expectArgs(input, 3, "get");

		String fileHash = ParserUtils.parseKeyCheck(splitted[1]);
		List<Integer> indexList = ParserUtils.parseBufferMap(
				input.substring(splitted[0].length() + splitted[1].length() + 2)
		);

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
