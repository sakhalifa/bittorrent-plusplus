package fr.ystat.files;

import fr.ystat.commands.CommandAnnotationCollector;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.peer.commands.GetPiecesCommand;
import fr.ystat.peer.commands.HaveCommand;
import fr.ystat.peer.commands.InterestedCommand;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTests {

    private static Collection<DynamicTest> generateParsingTests(Map<String, Class<? extends IReceivableCommand>> cases) {
        return cases.entrySet()
                .stream()
                .map(
                        (entry) -> DynamicTest.dynamicTest(
                                String.format("%sParsingTest", entry.getValue().getName()),
                                () -> assertEquals(
                                        entry.getValue(),
                                        CommandAnnotationCollector.beginParsing(entry.getKey()).getClass()))
                ).toList();
    }

    @TestFactory
    public Collection<DynamicTest> parserDynamicTests() {
        // Creation Map
        Map<String, Class<? extends IReceivableCommand>> testCases = Map.of(
                "getpieces 012345678901234567890123456789ab [0]", GetPiecesCommand.class,
                "interested 012345678901234567890123456789ab", InterestedCommand.class,
                "have 012345678901234567890123456789ab [0]", HaveCommand.class
        );

        // Stream
        return generateParsingTests(testCases);

    }
}
