package fr.ystat.server.handler;

import fr.ystat.server.Server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {


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
				ReadCommandHandler handler = new ReadCommandHandler(clientChannel, server.getCounter());
				ByteBuffer buffer = ByteBuffer.allocate(32);
//				ReadWriteHandler handler = new ReadWriteHandler(clientChannel);
//
//				Map<String, Object> readInfo = new HashMap<>();
//				readInfo.put("action", "read");
//				readInfo.put("buffer", buffer);
//
				clientChannel.read(buffer, buffer, handler);

			});
		}
	}

	@Override
	public void failed(Throwable throwable, Void unused) {

	}
}
