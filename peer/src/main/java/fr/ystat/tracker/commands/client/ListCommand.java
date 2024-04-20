package fr.ystat.tracker.commands.client;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.parser.ParserUtils;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.FileProperties;
import fr.ystat.parser.ListParser;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.Pair;
import lombok.Getter;

import java.util.List;

class ListCommandParser implements ICommandParser{

	@Override
	public IReceivableCommand parse(String input) throws ParserException {
		ParserUtils.expectArgs(input, 2, "list");

		List<FileProperties> files = new ListParser<>((lst, idx) -> {
			if(lst.length - idx < 4)
				throw new InvalidInputException(input, "list.badElFormat." + idx);
			String filename = lst[idx];
			long length = Long.parseLong(lst[idx+1]);
			long pieceSize = Long.parseLong(lst[idx+2]);
			String hash = lst[idx+3];
			if(hash.length() != 32)
				throw new InvalidInputException(input, "list.badElFormat." + idx + ".badHashLength");
			return new Pair<>(new FileProperties(filename, length, pieceSize, hash), 4);
		}).parse(input.substring("list".length() + 1 + 1, input.length()-1));
		return new ListCommand(files);
	}
}

@Getter
@CommandAnnotation(name="list", parser = ListCommandParser.class)
public class ListCommand implements IReceivableCommand {
	private final List<FileProperties> fileProperties;

	public ListCommand(List<FileProperties> fileProperties) {
		this.fileProperties = fileProperties;
	}

	@Override
	public String apply() throws CommandException {
		throw new UnsupportedOperationException("Cannot use apply on 'list'");
	}
}
