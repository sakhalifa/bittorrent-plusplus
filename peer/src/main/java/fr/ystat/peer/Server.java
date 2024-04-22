package fr.ystat.peer;

import fr.ystat.Main;
import fr.ystat.handlers.ConnectionHandler;
import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executor;

@Getter
public class Server {
	private final AsynchronousServerSocketChannel serverChannel;
	private final CompletionHandler<AsynchronousSocketChannel, Void> connectionHandler;
	private final Counter counter;
	private final Executor executor;
	private final InetSocketAddress address;

	public Server(Executor threadExecutor) throws IOException {
		this.serverChannel = AsynchronousServerSocketChannel.open();
		this.address = new InetSocketAddress(Main.getConfigurationManager().peerPort());
		this.serverChannel.bind(this.address);
		this.counter = new Counter();
		this.executor = threadExecutor;
		this.connectionHandler = new ConnectionHandler(this);
	}

	public void serve() throws IOException {
		while (true) {
			System.out.printf("Listening on %s:%d%n", address.getHostName(), address.getPort());
			serverChannel.accept(null, this.connectionHandler);
			System.in.read();
		}
	}
}
