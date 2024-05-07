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
import java.util.concurrent.atomic.AtomicInteger;

public class Seeder {
	private final AsynchronousServerSocketChannel serverChannel;
	private final AtomicInteger numberOfConnections;

	public Seeder() throws IOException {
		this.numberOfConnections = new AtomicInteger(0);
		this.serverChannel = AsynchronousServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("0.0.0.0", Main.getConfigurationManager().peerPort());
		this.serverChannel.bind(address);
		Logger.info("Server started on port {}", Main.getConfigurationManager().peerPort());
	}

	public void serve() {
		Logger.debug("Waiting for connections");
		serverChannel.accept(this.serverChannel, new ClientHandler(numberOfConnections));
	}
}
