package fr.ystat.server;

import fr.ystat.server.handler.ConnectionHandler;
import lombok.Getter;

import java.io.IOException;
import java.net.*;
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

	public Server(Executor threadExecutor, int port) throws IOException {
		this.serverChannel = AsynchronousServerSocketChannel.open();
		this.address = new InetSocketAddress(port);
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
