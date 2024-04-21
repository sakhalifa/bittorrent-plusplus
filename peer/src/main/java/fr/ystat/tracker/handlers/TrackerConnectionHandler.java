package fr.ystat.tracker.handlers;

import fr.ystat.Main;
import fr.ystat.commands.OkCommand;
import fr.ystat.files.FileInventory;
import fr.ystat.handlers.GenericCommandHandler;
import fr.ystat.tracker.commands.server.AnnounceCommand;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class TrackerConnectionHandler implements CompletionHandler<Void, AsynchronousSocketChannel> {

	private final Runnable onConnect;

	public TrackerConnectionHandler(Runnable onConnect){
		this.onConnect = onConnect;
	}

	@Override
	public void completed(Void unused, AsynchronousSocketChannel channel) {
		AnnounceCommand announce = new AnnounceCommand(Main.getConfigurationManager().getPeerPort(), FileInventory.getInstance().getAllFiles());
//		channel.write(SerializationUtils.CHARSET.encode(announce.serialize()), channel, new TrackerAnnounceHandler());
		GenericCommandHandler.sendCommand(channel, announce, OkCommand.class,
				(ignore) -> onConnect.run(),
				() -> {
					System.err.println("Error while sending command 'announce'! Aborting");
					try {
						channel.close();
						System.exit(1);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

				});
	}

	@Override
	public void failed(Throwable throwable, AsynchronousSocketChannel channel) {
		System.err.println("Cannot connect to tracker :(");
		System.exit(1);
	}
}
