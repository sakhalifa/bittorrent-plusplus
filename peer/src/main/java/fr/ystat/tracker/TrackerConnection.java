package fr.ystat.tracker;

import fr.ystat.Main;
import fr.ystat.commands.OkCommand;
import fr.ystat.files.FileInventory;
import fr.ystat.io.exceptions.ConnectionClosedByRemoteException;
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
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

public class TrackerConnection {

	private final AsynchronousSocketChannel announceUpdateChannel;
	private final AsynchronousSocketChannel requestChannel;

	public TrackerConnection(InetAddress trackerAddress, int port, Runnable onConnect) throws IOException {
		var trackerAddr = new InetSocketAddress(trackerAddress, port);
		this.requestChannel = AsynchronousSocketChannel.open();
		this.requestChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		this.announceUpdateChannel = AsynchronousSocketChannel.open();
		this.announceUpdateChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		Logger.trace("Connecting to {}:{}", trackerAddress, port);
		this.announceUpdateChannel.connect(trackerAddr, this.announceUpdateChannel, new TrackerConnectionHandler(() -> {
			this.requestChannel.connect(trackerAddr, null, new CompletionHandler<Void, Void>() {
				@Override
				public void completed(Void unused, Void unused2) {
					onConnect.run();
				}

				@Override
				public void failed(Throwable throwable, Void unused) {
					Logger.error(throwable, "Could not connect to tracker!");
					System.exit(1);
				}
			});
			new Thread(() -> {
				try {
					Thread.sleep(Main.getConfigurationManager().updateTrackerIntervalMS());
				} catch (InterruptedException ignored) {
				}
				this.scheduleUpdates();
			}).start();
		}));

	}

	public void sendLook(LookCommand lc, Consumer<ListCommand> onSuccess, Consumer<Throwable> onFailure) {
		GenericCommandHandler.sendCommand(this.requestChannel, lc, ListCommand.class, onSuccess, onFailure);
	}

	public void sendGetFile(GetFileCommand gfc, Consumer<PeersCommand> onSuccess, Consumer<Throwable> onFailure) {
		GenericCommandHandler.sendCommand(this.requestChannel, gfc, PeersCommand.class, onSuccess, onFailure);
	}

	public void scheduleUpdates() {
		var update = new AnnounceCommand(Main.getConfigurationManager().peerPort(), FileInventory.getInstance().getAllFiles(), true);
		GenericCommandHandler.sendCommand(this.announceUpdateChannel, update, OkCommand.class,
				(unused) -> {
					try {
						Thread.sleep(Main.getConfigurationManager().updateTrackerIntervalMS());
						this.scheduleUpdates();
					} catch (InterruptedException ignored) {
					}
				},
				(throwable) -> {
					if (throwable instanceof ConnectionClosedByRemoteException) {
						Logger.error("Tracker has gone down. Exiting...");
						try {
							announceUpdateChannel.close();
						} catch (IOException ignored) {
							System.exit(1);
						}
						System.exit(1);
					}
					Logger.warn("There was a problem with the update command.");
					try {
						Thread.sleep(Main.getConfigurationManager().updateTrackerIntervalMS());
						this.scheduleUpdates();
					} catch (InterruptedException ignored) {
					}
				});
	}
}
