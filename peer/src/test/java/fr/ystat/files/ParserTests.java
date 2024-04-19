package fr.ystat.files;

import fr.ystat.commands.CommandAnnotationCollector;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.OkCommand;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.peer.commands.DataCommand;
import fr.ystat.peer.commands.GetPiecesCommand;
import fr.ystat.peer.commands.HaveCommand;
import fr.ystat.peer.commands.InterestedCommand;
import fr.ystat.tracker.commands.client.ListCommand;
import fr.ystat.tracker.commands.client.PeersCommand;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTests {

    @FunctionalInterface
    interface TestFunction<Expected, Actual> {
        void test(Expected e, Actual a) throws Exception;
    }

    private static <Expected, Actual> Stream<DynamicTest> genericTests(
            Map<Expected, Actual> cases,
            TestFunction<Expected, Actual> testExec,
            BiFunction<Expected, Actual, String> getTestName
    ) {
        return cases.entrySet()
                .stream()
                .map((entry) -> DynamicTest.dynamicTest(
                        getTestName.apply(entry.getKey(), entry.getValue()),
                        () -> testExec.test(entry.getKey(), entry.getValue())
                ));
    }

    private static Stream<DynamicTest> generateParsingExceptionTests(Map<String, Class<? extends ParserException>> cases) {
        return genericTests(cases,
                (input, exception) -> assertThrows(exception, () -> CommandAnnotationCollector.beginParsing(input)),
                (input, exception) -> String.format("%sParsing%sTest", input.split(" ")[0], exception.getName()));
    }

    private static Stream<DynamicTest> generateParsingTests(Map<String, Class<? extends IReceivableCommand>> cases) {
        return genericTests(cases,
                (input, commandClass) -> assertEquals(commandClass, CommandAnnotationCollector.beginParsing(input).getClass()),
                (unused, commandClass) -> String.format("%sParsingTest", commandClass.getName()));
    }

    @TestFactory
    public Stream<DynamicTest> parserDynamicTests() {
        // Creation Map
        Map<String, Class<? extends IReceivableCommand>> testCases = Map.of(
                "getpieces 012345678901234567890123456789ab [1 1 52 3]", GetPiecesCommand.class,
                "interested 012345678901234567890123456789ab", InterestedCommand.class,
                "have 012345678901234567890123456789ab [0 1 12 20]", HaveCommand.class,

                "list [name 64512 1024 012345678901234567890123456789ab name 64512 1024 012345678901234567890123456789ab]", ListCommand.class,
                "peers 012345678901234567890123456789ab [127.0.0.1:0001 127.0.0.1:0001 127.0.0.1:0001]", PeersCommand.class,
                "ok", OkCommand.class,
                "data 012345678901234567890123456789ab [TODO :D]", DataCommand.class
        );
        // Stream
        return generateParsingTests(testCases);
    }

    @TestFactory
    public Stream<DynamicTest> parserExceptionsDynamicTests() {
        // Creation Map
        Map<String, Class<? extends ParserException>> testCases = Map.of(
                "getpieces 012345678901234567890123456789ab [1 a1 52 3]", InvalidInputException.class,
                "interestd 012345678901234567890123456789ab", ParserException.class,
                "interested e012345678901234567890123456789ab", InvalidInputException.class,
                "have 012345678901234567890123456789ab []", InvalidInputException.class,

                "list [name 64512 1024 012345678901234567890123456789ab 64512 1024 012345678901234567890123456789ab]", InvalidInputException.class,
                "peers 012345678901234567890123456789ab [127.0.0.1: 127.0.0.1:0001 127.0.0.1:0001]", InvalidInputException.class
        );
        // Stream
        return generateParsingExceptionTests(testCases);
    }
}
