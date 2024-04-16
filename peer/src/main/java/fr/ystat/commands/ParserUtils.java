package fr.ystat.commands;

import fr.ystat.parser.ListParser;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.Pair;

import java.util.List;

public class ParserUtils {
    public static String parseKeyCheck(String expectKey) throws InvalidInputException {
        if (expectKey.matches("[a-z0-9]{32}")) {
            return expectKey;
        }
        throw new InvalidInputException(String.format("%s is not a valid key format!", expectKey));
    }

    public static String parseBufferMapCheck(String expectBufferMap) throws InvalidInputException {
        if (expectBufferMap.matches("\\[[0-9]+( [0-9]+)*]")) {
            return expectBufferMap;
        }
        throw new InvalidInputException(String.format("%s is not a valid buffer map format!", expectBufferMap));
    }

    public static List<Integer> parseBufferMap(String expectedBufferMap) throws ParserException {
        String bufferMap = parseBufferMapCheck(expectedBufferMap);

        ListParser<Integer> indexListParser = new ListParser<>((integerList, idx) -> {
            try{
                int n = Integer.parseInt(integerList[idx]);
                return new Pair<>(n, 1);
            }catch(NumberFormatException ex){
                throw new InvalidInputException(String.join(" ", integerList));
            }
        });
        // Buffer map is of the form [ X Y Z ], so we want to strip it from the '[' & ']'
        return indexListParser.parse(bufferMap.substring(1, bufferMap.length() - 1));
    }
}
