package fr.ystat.files;

import fr.ystat.commands.CommandAnnotationCollector;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.OkCommand;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.parser.exceptions.UnknownCommandException;
import fr.ystat.peer.commands.DataCommand;
import fr.ystat.peer.commands.GetPiecesCommand;
import fr.ystat.peer.commands.HaveCommand;
import fr.ystat.peer.commands.InterestedCommand;
import fr.ystat.tracker.commands.client.ListCommand;
import fr.ystat.tracker.commands.client.PeersCommand;
import fr.ystat.util.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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

    private static Stream<DynamicTest> generateParsingExceptionTests(Map<String, Exception> cases) {
        return genericTests(cases,
                (input, exception) -> assertThrowsExactly(exception.getClass(), () -> CommandAnnotationCollector.beginParsing(input), input),
                (input, exception) -> String.format("%sParsing%sTest", input.split(" ")[0], exception.getClass().getName()));
    }

    private static Stream<DynamicTest> generateParsingTests(Map<String, Class<? extends IReceivableCommand>> cases) {
        return genericTests(cases,
                (input, commandClass) -> assertEquals(commandClass, CommandAnnotationCollector.beginParsing(input).getClass(), input),
                (unused, commandClass) -> String.format("%sParsingTest", commandClass.getName()));
    }

    @BeforeAll
    public static void addDummyFile(){
        // add 012345678901234567890123456789ab hash to avoid noSuchFile exceptions
        FileInventory.getInstance().addStockedFile(new StockedFile(new FileProperties("dummy.file", 11, 4, "012345678901234567890123456789ab")) {
            @Override
            public byte[] getPartition(int partitionIndex) {
                return new byte[0];
            }

            @Override
            public AtomicBitSet getBitSet() {
                return new AtomicBitSet(0);
            }
        });
    }

    @TestFactory
    public Stream<DynamicTest> parserDynamicTests() {
        // Creation Map

        AtomicBitSet bitSet = new AtomicBitSet(3);

        Map<String, Class<? extends IReceivableCommand>> testCases = Map.of(
                "getpieces 012345678901234567890123456789ab [1 1 52 3]\n", GetPiecesCommand.class,
                "interested 012345678901234567890123456789ab\n", InterestedCommand.class,
                "have 012345678901234567890123456789ab " + bitSet + "\n", HaveCommand.class,
                "list [name 64512 1024 012345678901234567890123456789ab name 64512 1024 012345678901234567890123456789ab]\n", ListCommand.class,
                "peers 012345678901234567890123456789ab [127.0.0.1:0001 127.0.0.1:0001 127.0.0.1:0001]\n", PeersCommand.class,
                "ok\n", OkCommand.class,
                "data 012345678901234567890123456789ab [1:abcd 2:abcd 3:abcd]\n", DataCommand.class
        );
        // Stream
        return generateParsingTests(testCases);
    }

    @TestFactory
    public Stream<DynamicTest> parserExceptionsDynamicTests() {

        enum ARG_TYPE {
            HASH(
                    "012345678901234567890123456789ab",
                    List.of(
                            new Pair<>("", new InvalidInputException("")),  // empty hash
                            new Pair<>("012345678901234567890123456789", new InvalidInputException("")), // Bad length 30 < 32
                            new Pair<>("012345678901234567890123456789aed", new InvalidInputException("")), // Bad length 33 > 32
                            new Pair<>("012345678901234567890123456789$£", new InvalidInputException(""))  // Invalid characters '$£'
                    )
            ),
            BUFFER_LIST(
                    "[1 2 3]",
                    List.of(
                            new Pair<>("", new InvalidInputException("")),  // no list
                            new Pair<>("[]", new InvalidInputException("")),  // Empty list
                            new Pair<>("[", new InvalidInputException("")), // Unclosed list
                            new Pair<>("[ 1 2 3 ]", new InvalidInputException("")),  // poorly formated
                            new Pair<>("[1 a]", new InvalidInputException(""))  // invalid int
                    )
            ),
            BITSET(
                    "000",
                    List.of(
                    )
            ),
            FILE_LIST(
                    "[name 64512 1024 012345678901234567890123456789ab]",
                    List.of(
                            new Pair<>("", new InvalidInputException("")),  // no list
                            new Pair<>("[]", new InvalidInputException("")),
                            new Pair<>("[64512 1024 012345678901234567890123456789ab]", new InvalidInputException("")),
                            new Pair<>("[name -64512 1024 012345678901234567890123456789ab]", new IllegalArgumentException()),
                            new Pair<>("[name 64512 0 012345678901234567890123456789ab]", new IllegalArgumentException()),
                            new Pair<>("[name 64512 1024 012345678901234567890123456789absqd]", new InvalidInputException("")),
                            new Pair<>("[name 64512 1024 012345 67890123456 7890123456789ab]", new InvalidInputException("")),
                            new Pair<>("[name 64512 1024 012345678901234567890123456789ab " +
                                    "64512 1024 012345678901234567890123456789ab]", new InvalidInputException(""))
                    )
            ),
            IP_LIST(
                    "[127.0.0.1:0001 127.0.0.1:0001 127.0.0.1:0001]",
                    List.of(
                            new Pair<>("", new InvalidInputException("")),  // no list
                            new Pair<>("[]", new InvalidInputException("[]")),
                            new Pair<>("[127.0.0.1: 127.0.0.1:0001 127.0.0.1:0001]", new InvalidInputException("")),
                            new Pair<>("[300.0000.0.0:1000]", new InvalidInputException(""))
                    )
            ),
            DATA_LIST(
                    "[1:abcd 2:abcd 3:abcd]",
                    List.of(
                            new Pair<>("", new InvalidInputException("")),  // no list
                            new Pair<>("[]", new InvalidInputException("[]")),  // no entry
                            new Pair<>("[1: 2:abcd 3:abcd]", new InvalidInputException("")),  // invalid entry
                            new Pair<>("[1:abcdef]", new InvalidInputException("")),  // wrong data size
                            new Pair<>("[1:abcd 1:abcd 3:abcd]", new IllegalStateException("")) // Duplicate entries
                    )
            )
            ;
            final String valid;
            final List<Pair<String, ? extends Exception>> invalids;

            ARG_TYPE(String valid, List<Pair<String, ? extends Exception>> invalids) {
                this.valid = valid;
                this.invalids = invalids;
            }
        }

        List<Pair<String, List<ARG_TYPE>>> commandsToTest = List.of(
                new Pair<>("getpieces", List.of(ARG_TYPE.HASH, ARG_TYPE.BUFFER_LIST)),
                new Pair<>("interested", List.of(ARG_TYPE.HASH)),
                new Pair<>("have", List.of(ARG_TYPE.HASH, ARG_TYPE.BITSET)),
                new Pair<>("list", List.of(ARG_TYPE.FILE_LIST)),
                new Pair<>("peers", List.of(ARG_TYPE.HASH, ARG_TYPE.IP_LIST)),
                new Pair<>("data", List.of(ARG_TYPE.HASH, ARG_TYPE.DATA_LIST))
        );

        // Creation Map
        Map<String, Exception> testCases = new HashMap<>();

        for (Pair<String, List<ARG_TYPE>> stringListPair : commandsToTest) {

            List<ARG_TYPE> args = stringListPair.getSecond();

            List<String> cmd = new ArrayList<>(List.of(stringListPair.getFirst()));
            cmd.addAll(args.stream().map((a) -> a.valid).toList());

            ListIterator<ARG_TYPE> it = args.listIterator();

            while (it.hasNext()) {

                int i = it.nextIndex();
                ARG_TYPE arg = it.next();

                 Map<String, ? extends Exception> cmdsWithError = arg.invalids.stream().map((a) -> {
                    ArrayList<String> newCmd = new ArrayList<>(cmd);
                    newCmd.set(i + 1, a.getFirst());
                    return new Pair<>(String.join(" ", newCmd) + "\n", a.getSecond());
                 }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

                testCases.putAll(cmdsWithError);
            }
        }

        // gibberish cases
        testCases.put("sqfdojf sdksdlfj\n", new UnknownCommandException(""));
        testCases.put("\n", new UnknownCommandException(""));
        testCases.put("intrested 012345678901234567890123456789ab\n", new UnknownCommandException(""));

        // Stream
        return generateParsingExceptionTests(testCases);
    }
}
