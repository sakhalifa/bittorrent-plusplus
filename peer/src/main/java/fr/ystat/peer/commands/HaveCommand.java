package fr.ystat.peer.commands;

import fr.ystat.commands.*;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.*;
import fr.ystat.parser.ParserUtils;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.peer.commands.exceptions.NoSuchFileException;
import fr.ystat.util.SerializationUtils;
import lombok.Getter;

import java.nio.ByteBuffer;

class HaveParser implements ICommandParser {

    @Override
    public IReceivableCommand parse(String input) throws ParserException {

        String[] splitted = ParserUtils.expectArgs(input, 3, "have");

        String fileHash = ParserUtils.parseKeyCheck(splitted[1]);

        StockedFile file = FileInventory.getInstance().getStockedFile(fileHash);
        if(file == null)
            throw new InvalidInputException(input, "have.noSuchFile");

        FileProperties fp = file.getProperties();
        long numPieces = (fp.getSize() + fp.getPieceSize() - 1) / fp.getPieceSize();

        // "{splitted[0]} {splitted[1} <BUFFERMAP>" -> splitted[0].length + 1 + splitted[1].length + 1
        String buffMapStr = input.substring(splitted[0].length() + 1 + splitted[1].length() + 1);
        ByteBuffer buffMapBuff = SerializationUtils.CHARSET.encode(buffMapStr);

        AtomicBitSet bitset = new AtomicBitSet(buffMapBuff, (int)numPieces);
        return new HaveCommand(fileHash, bitset);
    }
}


@Getter
@CommandAnnotation(name = "have", parser = HaveParser.class)
public class HaveCommand implements ICommand {

    private final String key;
    private final AtomicBitSet bitSet;

    public HaveCommand(String key, AtomicBitSet bitSet){
        this.key = key;
        this.bitSet = bitSet;
    }

    public HaveCommand(DownloadedFile file){
        this.key = file.getProperties().getHash();
        this.bitSet = file.getBitSet();
    }

    @Override
    public String apply() throws CommandException {
        StockedFile file = FileInventory.getInstance().getStockedFile(key);
        if(file == null)
            throw new NoSuchFileException(key); // TODO maybe have an empty buffermap instead...
        AtomicBitSet myBitset = file.getBitSet();
        return new HaveCommand(key, myBitset).serialize();
    }

    @Override
    public String serialize() {
        return String.format("%s %s %s", ICommand.super.serialize(), key, bitSet.toString());
    }
}
