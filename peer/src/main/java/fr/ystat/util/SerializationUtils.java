package fr.ystat.util;

import fr.ystat.commands.ISendableCommand;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SerializationUtils {
	public static final Charset CHARSET = StandardCharsets.ISO_8859_1;

	public static <T> Stream<T> filterByType(Stream<? super T> stream, Class<T> clazz) {
		return stream
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}

	public static String listToString(List<?> lst) { // TODO: Benchmark to see if it's really necessary with streamToString
		StringBuilder sb = new StringBuilder("[");
		for (var e : lst) {
			sb.append(e.toString());
			sb.append(" ");
		}
		if (sb.length() > 1) // edge case if it's empty
			sb.setLength(sb.length() - 1); // remove last space
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Create a InetsocketAddress from "ip:port" string.
	 * Very hacky but well. it is what it is
	 *
	 * @param hostAndPort
	 * @return
	 */
	public static InetSocketAddress socketAddressFromString(String hostAndPort) throws URISyntaxException {
		URI uri = new URI("my://" + hostAndPort);
		String host = uri.getHost();
		int port = uri.getPort();

		if (uri.getHost() == null || uri.getPort() == -1) {
			throw new URISyntaxException(uri.toString(),
					"URI must have host and port parts");
		}
		return new InetSocketAddress(host, port);
	}

	public static String streamToString(Stream<?> stream) {
		return String.format("[%s]", stream.map(Object::toString).collect(Collectors.joining(" ")));
	}

	public static ByteBuffer toByteBuffer(ISendableCommand command){
		return SerializationUtils.CHARSET.encode(command.serialize() + "\n");
	}
}
