package fr.ystat.peer.commands;

import fr.ystat.commands.*;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.FileInventory;
import fr.ystat.files.StockedFile;
import fr.ystat.files.exceptions.PartitionException;
import fr.ystat.parser.ParserUtils;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.peer.commands.exceptions.InvalidPartition;
import fr.ystat.peer.commands.exceptions.NoSuchFileException;
import fr.ystat.util.SerializationUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class GetPiecesParser implements ICommandParser{

	@Override
	public IReceivableCommand parse(String input) throws ParserException {
		String[] splitted = ParserUtils.expectArgs(input, 3, "get");

		String fileHash = ParserUtils.parseKeyCheck(splitted[1]);
		List<Integer> indexList = ParserUtils.parseBufferMap(
				input.substring(splitted[0].length() + splitted[1].length() + 2),
				"get"
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
		StockedFile file = FileInventory.getInstance().getStockedFile(key);
		if(file == null)
			throw new NoSuchFileException(key);
		List<Map.Entry<Integer, ByteBuffer>> dataMapEntryList = new ArrayList<>(indexes.size());
		for(int i : indexes){
			try {
				byte[] data = file.getPartition(i);
				dataMapEntryList.add(Map.entry(i, ByteBuffer.wrap(data)));
			} catch (PartitionException e) {
				throw new InvalidPartition(file, i);
			} catch (IOException e) {
				throw new RuntimeException(e); // Hope it never happens :/
			}
		}
		return new DataCommand(key, dataMapEntryList).serialize();
	}

	@Override
	public String serialize() {
		return String.format("%s %s %s", ICommand.super.serialize(), key, SerializationUtils.listToString(indexes));
	}
}
