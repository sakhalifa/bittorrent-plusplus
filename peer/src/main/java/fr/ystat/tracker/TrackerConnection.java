package fr.ystat.tracker;

import fr.ystat.tracker.commands.client.ListCommand;
import fr.ystat.tracker.commands.client.PeersCommand;
import fr.ystat.tracker.commands.server.GetFileCommand;
import fr.ystat.tracker.commands.server.LookCommand;
import fr.ystat.handlers.GenericCommandHandler;
import fr.ystat.tracker.handlers.TrackerConnectionHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.function.Consumer;

public class TrackerConnection {

	private final AsynchronousSocketChannel channel;

	public TrackerConnection(InetAddress trackerAddress, int port, Runnable onConnect) throws IOException {
		this.channel = AsynchronousSocketChannel.open();
		this.channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		this.channel.connect(new InetSocketAddress(trackerAddress, port), this.channel, new TrackerConnectionHandler(onConnect));
	}

	public void sendLook(LookCommand lc, Consumer<ListCommand> onSuccess, Runnable onFailure) {
		GenericCommandHandler.sendCommand(this.channel, lc, ListCommand.class, onSuccess, onFailure);
	}

	public void sendGetFile(GetFileCommand gfc, Consumer<PeersCommand> onSuccess, Runnable onFailure) {
		GenericCommandHandler.sendCommand(this.channel, gfc, PeersCommand.class, onSuccess, onFailure);
	}
}
