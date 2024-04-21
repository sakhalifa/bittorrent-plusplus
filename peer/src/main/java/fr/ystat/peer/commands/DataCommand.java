package fr.ystat.peer.commands;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.FileInventory;
import fr.ystat.files.StockedFile;
import fr.ystat.parser.ListParser;
import fr.ystat.parser.ParserUtils;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.Pair;
import fr.ystat.util.SerializationUtils;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
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
			throw new InvalidInputException("[data.noSuchFile] File not found");

		long dataLen = file.getProperties().getPieceSize();
		List<Map.Entry<Integer, ByteBuffer>> dataList = new ArrayList<>();
		int startIdx = splitted[0].length() + 1 + splitted[1].length() + 1 + 1;
		try {
			String toRead = input.substring(startIdx);
			while (!toRead.isEmpty()) {
				int firstSepIdx = toRead.indexOf(':');
				if(firstSepIdx == -1)
					throw new InvalidInputException("[data.badFormat.noDataSep] bad format");
				String pieceNumStr = toRead.substring(0, firstSepIdx);
				int pieceNum;
				try{
					pieceNum = Integer.parseInt(pieceNumStr);
				}catch(NumberFormatException e){
					throw new InvalidInputException("[data.badFormat.pieceNumNotANum] bad format");
				}
				String data = toRead.substring(firstSepIdx+1, (int) (firstSepIdx+1+dataLen));
				dataList.add(Map.entry(pieceNum, SerializationUtils.CHARSET.encode(data)));
				toRead = toRead.substring((int) (firstSepIdx+1+dataLen+1));
			}
		}catch (StringIndexOutOfBoundsException e){
			throw new InvalidInputException("[data.badFormat] bad format");
		}

//		List<Map.Entry<Integer, ByteBuffer>> dataList = new ListParser<>((lst, idx) -> {
//			System.out.printf("[%d] %s%n", idx, lst[idx]);
//			String[] split = lst[idx].split(":");
//			int firstSeparator = lst[idx].lastIndexOf(':');
//			if(firstSeparator == -1) // cannot check splitted.length due to edge case "1::::::::" which returns {"1"}
//				throw new InvalidInputException("[data.badFormat] Invalid format");
//			String pieceNumStr = split[0];
//			int pieceNum;
//			try{
//				pieceNum = Integer.parseInt(pieceNumStr);
//			}catch(NumberFormatException e){
//				throw new InvalidInputException("[data.badFormat] Invalid format");
//			}
//			StringBuilder reading = new StringBuilder();
//			for(int i = 1; i<split.length; ++i){
//				reading.append(split[i]);
//				reading.append(":");
//			}
//			reading.deleteCharAt(reading.length()-1);
//			int off = 1;
//			while(reading.length() != file.getProperties().getPieceSize() && idx+off < lst.length){
//				reading.append(' ');
//				String str = lst[idx + off];
//				reading.append(str);
//				++off;
//			}
//			while(reading.length() != file.getProperties().getPieceSize())
//				reading.append(' '); // EDGE CASE I LOVE IT YAY
//			return new Pair<>(Map.entry(pieceNum, SerializationUtils.CHARSET.encode(reading.toString())), off);
//		}).parse(input.substring(splitted[0].length() + 1 + splitted[1].length() + 1 + 1, input.length() - 1));
		return new DataCommand(dataList);
	}
}


@Getter
@CommandAnnotation(name = "data", parser = DataParser.class)
public class DataCommand implements IReceivableCommand {
	private final Map<Integer, ByteBuffer> dataList;

	public DataCommand(List<Map.Entry<Integer, ByteBuffer>> dataList) throws IllegalArgumentException {
		this.dataList = dataList
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public String apply() throws CommandException {


		return "";  // You don't want to send it, don't you ? :3
	}
}
