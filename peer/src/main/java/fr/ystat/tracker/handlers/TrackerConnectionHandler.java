package fr.ystat.tracker.handlers;

import fr.ystat.Main;
import fr.ystat.commands.ICommand;
import fr.ystat.files.FileInventory;
import fr.ystat.tracker.commands.server.AnnounceCommand;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class TrackerConnectionHandler implements CompletionHandler<Void, AsynchronousSocketChannel> {
	@Override
	public void completed(Void unused, AsynchronousSocketChannel channel) {
		ICommand announce = new AnnounceCommand(Main.getConfigurationManager().getPeerPort(), FileInventory.getInstance().getAllFiles());
		channel.write(StandardCharsets.ISO_8859_1.encode(announce.serialize()));
	}

	@Override
	public void failed(Throwable throwable, AsynchronousSocketChannel channel) {
		System.err.println("Cannot connect to tracker :(");
		System.exit(1);
	}
}
