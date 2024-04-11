package fr.ystat.tracker.commands.client;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.DownloadedFile;
import fr.ystat.files.FileProperties;
import fr.ystat.files.StockedFile;
import fr.ystat.parser.ListParser;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.server.Counter;
import fr.ystat.util.Pair;
import fr.ystat.util.SerializationUtils;
import lombok.Getter;

import java.util.List;

class ListCommandParser implements ICommandParser{

	@Override
	public ICommand parse(String input) throws ParserException {
		String[] splitted = input.split(" ");
		if(splitted.length < 2)
			throw new InvalidInputException(input, "list.badFormat");
		List<FileProperties> files = new ListParser<>(((lst, idx) -> {
			if(lst.length - idx < 4)
				throw new InvalidInputException(input, "list.badElFormat." + idx);
			String filename = lst[idx];
			long length = Long.parseLong(lst[idx+1]);
			long pieceSize = Long.parseLong(lst[idx+2]);
			String hash = lst[idx+3];
			return new Pair<>(new FileProperties(filename, length, pieceSize, hash), 4);
		})).parse(input.substring("list".length() + 1 + 1, input.length()-1));
		return new ListCommand(files);
	}
}

@CommandAnnotation(name="list", parser = ListCommandParser.class)
public class ListCommand implements ICommand {
	private final List<FileProperties> files;

	public ListCommand(List<FileProperties> files) {
		this.files = files;
	}

	@Override
	public String apply(Counter counter) throws CommandException {
		// TODO: use apply ?
		return null;
	}

	@Override
	public String serialize() {
		return String.format("list %s", SerializationUtils.listToString(files));
	}
}
