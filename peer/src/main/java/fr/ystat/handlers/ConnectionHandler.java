package fr.ystat.handlers;

import fr.ystat.peer.Server;
import fr.ystat.peer.seeder.handlers.ReadCommandHandler;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {

	public static int BUFFER_SIZE = 1024;
	private final Server server;

	public ConnectionHandler(Server server){
		this.server = server;
	}


	@Override
	public void completed(AsynchronousSocketChannel clientChannel, Void unused) {
		if (server.getServerChannel().isOpen()) {
			server.getServerChannel().accept(null, this);
		}

		if ((clientChannel != null) && (clientChannel.isOpen())) {
			server.getExecutor().execute(() -> {
				try {
					System.out.printf("[%s] ConnectionHandler on Thread %d%n", clientChannel.getRemoteAddress(), Thread.currentThread().getId());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				ReadCommandHandler handler = new ReadCommandHandler(clientChannel, server.getCounter());
				handler.startReading();
			});
		}
	}

	@Override
	public void failed(Throwable throwable, Void unused) {

	}
}
