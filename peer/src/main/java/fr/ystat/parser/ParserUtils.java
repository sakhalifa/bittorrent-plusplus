package fr.ystat.parser;

import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.Pair;

import java.util.List;

public class ParserUtils {

    private static String formatSource(String source, String errorType){
        if (source.isEmpty()) {
            source = errorType;
        } else {
            source += "." + errorType;
        }
        return source;
    }

    public static String parseKeyCheck(String expectKey, String source) throws InvalidInputException {
        if (expectKey.matches("[a-z0-9]{32}")) {
            return expectKey;
        }
        throw new InvalidInputException(expectKey, formatSource(source, "badKeyFormat"));
    }

    public static String parseKeyCheck(String expectKey) throws InvalidInputException {
        return parseKeyCheck(expectKey, "");
    }

    public static String parseBufferMapCheck(String expectBufferMap) throws InvalidInputException {
        if (expectBufferMap.matches("\\[[0-9]+( [0-9]+)*]")) {
            return expectBufferMap;
        }
        throw new InvalidInputException(expectBufferMap, "list.badElFormat");
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

    public static String[] expectArgs(String input, int expectedArgsAmount, String source) throws InvalidInputException {
        String[] splitted = input.split(" ");
        if(splitted.length < expectedArgsAmount)
            throw new InvalidInputException(input, formatSource(source, "badformat"));
        return splitted;
    }
}
