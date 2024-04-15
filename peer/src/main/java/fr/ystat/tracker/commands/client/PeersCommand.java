package fr.ystat.tracker.commands.client;

import fr.ystat.commands.*;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.FileInventory;
import fr.ystat.parser.ListParser;
import fr.ystat.parser.exceptions.InvalidInputException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.server.Counter;
import fr.ystat.util.Pair;
import fr.ystat.util.SerializationUtils;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.util.List;

class PeersCommandParser implements ICommandParser {
	@Override
	public IReceivableCommand parse(String input) throws ParserException {
		String[] splitted = input.split(" ");
		if(splitted.length < 3)
			throw new InvalidInputException(input, "peers.badformat");
		String hash = splitted[1];
		if(hash.length() != 32)
			throw new InvalidInputException(input, "peers.badkeylength");
		List<InetSocketAddress> peers = new ListParser<>(((lst, idx) -> {
			String cur = lst[idx];
			try {
				return new Pair<>(SerializationUtils.socketAddressFromString(cur), 1);
			} catch (URISyntaxException e) {
				throw new InvalidInputException(input, "peers.badHost." + idx);
			}
		})).parse(input.substring("peers".length() + hash.length() + 2 + 1, input.length()-1));
		return new PeersCommand(hash, peers);
	}
}

@Getter
@CommandAnnotation(name = "peers", parser = PeersCommandParser.class)
public class PeersCommand implements IReceivableCommand {
	private final List<InetSocketAddress> peers;
	private final String hash;
	public PeersCommand(String hash, List<InetSocketAddress> peers){
		this.hash = hash;
		this.peers = peers;
	}

	@Override
	public String apply() throws CommandException {
		// TODO: use peers ?
		return null;
	}
}
