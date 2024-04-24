package fr.ystat.tracker.handlers;

import fr.ystat.Main;
import fr.ystat.commands.OkCommand;
import fr.ystat.files.FileInventory;
import fr.ystat.handlers.GenericCommandHandler;
import fr.ystat.tracker.commands.server.AnnounceCommand;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class TrackerConnectionHandler implements CompletionHandler<Void, AsynchronousSocketChannel> {

	private final Runnable onConnect;

	public TrackerConnectionHandler(Runnable onConnect) {
		this.onConnect = onConnect;
	}

	@Override
	public void completed(Void unused, AsynchronousSocketChannel channel) {
		try {
			Logger.debug("Connected to {}. Sending announce command", channel.getRemoteAddress());
		} catch (IOException ignored) {
		}
		AnnounceCommand announce = new AnnounceCommand(Main.getConfigurationManager().peerPort(), FileInventory.getInstance().getAllFiles());
		GenericCommandHandler.sendCommand(channel, announce, OkCommand.class,
				(ignore) -> {
					Logger.debug("Announce succeeded. Executing connection callback");
					onConnect.run();
				},
				(throwable) -> {
					Logger.error("Error while sending command '{}'! Aborting", announce.serialize());
					try {
						channel.close();
						System.exit(1);
					} catch (IOException e) {
						System.exit(1);
					}

				});
	}

	@Override
	public void failed(Throwable throwable, AsynchronousSocketChannel channel) {
		Logger.error("Could not connect to tracker!");
		System.exit(1);
	}
}
