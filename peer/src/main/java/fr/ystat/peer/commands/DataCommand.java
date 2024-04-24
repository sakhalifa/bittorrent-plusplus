package fr.ystat.peer.commands;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.FileInventory;
import fr.ystat.files.StockedFile;
import fr.ystat.parser.ParserUtils;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.SerializationUtils;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


class DataParser implements ICommandParser {

	@Override
	public IReceivableCommand parse(String input) throws ParserException {

		String[] splitted = ParserUtils.expectArgs(input, 3, "data");

		String fileHash = ParserUtils.parseKeyCheck(splitted[1]);

		StockedFile file = FileInventory.getInstance().getStockedFile(fileHash);
		if (file == null)
			throw new InvalidInputException(input, "data.noSuchFile");

		long dataLen = file.getProperties().getPieceSize();
		List<Map.Entry<Integer, ByteBuffer>> dataList = new ArrayList<>();
		int startIdx = splitted[0].length() + 1 + splitted[1].length() + 1 + 1;
		try {
			String toRead = input.substring(startIdx);
			while (!toRead.isEmpty()) {
				int firstSepIdx = toRead.indexOf(':');
				if (firstSepIdx == -1)
					throw new InvalidInputException(input, "data.badFormat.noDataSep");
				String pieceNumStr = toRead.substring(0, firstSepIdx);
				int pieceNum;
				try {
					pieceNum = Integer.parseInt(pieceNumStr);
				} catch (NumberFormatException e) {
					throw new InvalidInputException(input, "data.badFormat.pieceNumNotANum");
				}
				String data = toRead.substring(firstSepIdx + 1, (int) (firstSepIdx + 1 + dataLen));
				dataList.add(Map.entry(pieceNum, SerializationUtils.CHARSET.encode(data)));
				toRead = toRead.substring((int) (firstSepIdx + 1 + dataLen + 1));
			}
		} catch (StringIndexOutOfBoundsException e) {
			throw new InvalidInputException(input, "data.badFormat");
		}
		return new DataCommand(fileHash, dataList);
	}
}


@Getter
@CommandAnnotation(name = "data", parser = DataParser.class)
public class DataCommand implements ICommand {
	private final String fileHash;
	private final Map<Integer, ByteBuffer> dataMap;

	public DataCommand(String fileHash, List<Map.Entry<Integer, ByteBuffer>> dataMap) throws IllegalArgumentException {
		this.fileHash = fileHash;
		this.dataMap = dataMap
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public String apply() throws CommandException {
		throw new UnsupportedOperationException("Cannot use apply() on data command");
	}

	private String dataMapToString() {
		return SerializationUtils.streamToString(dataMap.entrySet().stream()
				.map((e) -> new Object() {
					@Override
					public String toString() {
						return e.getKey() + ":" + SerializationUtils.CHARSET.decode(e.getValue());
					}
				}));
	}

	@Override
	public String serialize() {
		return String.format("%s %s %s", ICommand.super.serialize(), fileHash, dataMapToString());
	}
}
