package fr.ystat.peer.seeder;

import fr.ystat.Main;
import fr.ystat.peer.seeder.handlers.ClientHandler;
import lombok.Getter;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executor;

public class Seeder {
	private final AsynchronousServerSocketChannel serverChannel;
	private final CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> connectionHandler;

	public Seeder() throws IOException {
		this.serverChannel = AsynchronousServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("0.0.0.0", Main.getConfigurationManager().peerPort());
		this.serverChannel.bind(address);
		this.connectionHandler = new ClientHandler();
		Logger.info("Server started on port {}", Main.getConfigurationManager().peerPort());
	}

	public void serve() {
		Logger.debug("Waiting for connections");
		serverChannel.accept(this.serverChannel, this.connectionHandler);
	}
}
