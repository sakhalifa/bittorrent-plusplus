package fr.ystat.tracker.commands.client;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.ICommandParser;
import fr.ystat.commands.exceptions.CommandException;
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
	public ICommand parse(String input) throws ParserException {
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

@CommandAnnotation(name = "peers", parser = PeersCommandParser.class)
public class PeersCommand implements ICommand {
	private final List<InetSocketAddress> peers;
	private final String hash;
	public PeersCommand(String hash, List<InetSocketAddress> peers){
		this.hash = hash;
		this.peers = peers;
	}

	@Override
	public String apply(Counter counter) throws CommandException {
		// TODO: use peers ?
		return null;
	}

	@Override
	public String serialize() {
		return String.format("peers %s %s", hash, SerializationUtils.listToString(peers));
	}
}
