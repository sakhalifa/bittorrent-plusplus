package fr.ystat.tracker;

import fr.ystat.Main;
import fr.ystat.commands.OkCommand;
import fr.ystat.files.FileInventory;
import fr.ystat.tracker.commands.client.ListCommand;
import fr.ystat.tracker.commands.client.PeersCommand;
import fr.ystat.tracker.commands.server.AnnounceCommand;
import fr.ystat.tracker.commands.server.GetFileCommand;
import fr.ystat.tracker.commands.server.LookCommand;
import fr.ystat.handlers.GenericCommandHandler;
import fr.ystat.tracker.handlers.TrackerConnectionHandler;
import org.tinylog.Logger;

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
		Logger.trace("Connecting to {}:{}", trackerAddress, port);
		this.channel.connect(new InetSocketAddress(trackerAddress, port), this.channel, new TrackerConnectionHandler(onConnect));
	}

	public void sendLook(LookCommand lc, Consumer<ListCommand> onSuccess, Runnable onFailure) {
		GenericCommandHandler.sendCommand(this.channel, lc, ListCommand.class, onSuccess, onFailure);
	}

	public void sendGetFile(GetFileCommand gfc, Consumer<PeersCommand> onSuccess, Runnable onFailure) {
		GenericCommandHandler.sendCommand(this.channel, gfc, PeersCommand.class, onSuccess, onFailure);
	}

	public void scheduleUpdates() {
		var tc = this;
		var update = new AnnounceCommand(Main.getConfigurationManager().peerPort(), FileInventory.getInstance().getAllFiles(), true);
		GenericCommandHandler.sendCommand(this.channel, update, OkCommand.class,
				(unused) -> {
					try {
						Thread.sleep(30000);
						tc.scheduleUpdates(); // avoid using this as it's ambiguous
					} catch (InterruptedException ignored) {}
				},
				() -> {
					Logger.warn("There was a problem with the update command.");
					try {
						Thread.sleep(30000);
						tc.scheduleUpdates(); // avoid using this as it's ambiguous
					} catch (InterruptedException ignored) {}
				});
	}
}
