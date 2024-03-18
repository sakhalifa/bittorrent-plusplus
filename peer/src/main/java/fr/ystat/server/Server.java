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

	public Server(Executor threadExecutor, int port) throws IOException {
		this.serverChannel = AsynchronousServerSocketChannel.open();
		this.serverChannel.bind(new InetSocketAddress(port));
		this.counter = new Counter();
		this.executor = threadExecutor;
		this.connectionHandler = new ConnectionHandler(this);
	}

	public void serve() throws IOException {
		while (true) {
			serverChannel.accept(null, this.connectionHandler);
			System.in.read();
		}
	}
}
