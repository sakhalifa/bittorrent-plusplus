package fr.ystat.tracker;

import fr.ystat.tracker.handlers.TrackerConnectionHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;

public class TrackerConnection {

	private final AsynchronousSocketChannel channel;

	public TrackerConnection(InetAddress trackerAddress, int port) throws IOException {
		this.channel = AsynchronousSocketChannel.open();
		this.channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		this.channel.connect(new InetSocketAddress(trackerAddress, port), this.channel, new TrackerConnectionHandler());
	}

}
