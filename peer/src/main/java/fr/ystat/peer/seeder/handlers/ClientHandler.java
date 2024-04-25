package fr.ystat.peer.seeder.handlers;

import fr.ystat.handlers.ExecuteCommandHandler;
import fr.ystat.handlers.ReadCommandHandler;
import fr.ystat.util.SerializationUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ClientHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

	private ReadCommandHandler readCommandHandler;
	private ExecuteCommandHandler executeCommandHandler;
	private AsynchronousSocketChannel clientChannel;
	private int failures = 1;

	private void retryOrDie() {
		if (++failures > 3) {
			try {
				clientChannel.close();
			} catch (IOException ignored) {
			}
		} else
			this.handleExchange();
	}

	@Override
	public void completed(AsynchronousSocketChannel clientChannel, AsynchronousServerSocketChannel serverChannel) {
		serverChannel.accept(serverChannel, new ClientHandler());
		this.clientChannel = clientChannel;
		try {
			Logger.debug("Client {} connected", clientChannel.getRemoteAddress());
		} catch (IOException ignored) {
		}
		executeCommandHandler = new ExecuteCommandHandler(clientChannel,
				() -> {
					this.handleExchange();
					failures = 1;
				},
				throwable -> retryOrDie()

		);
		readCommandHandler = new ReadCommandHandler(clientChannel,
				(command) -> {
					Logger.debug("Received command type {}", command.getClass().getSimpleName());
					executeCommandHandler.execute(command);
				},
				throwable -> {
					clientChannel.write(SerializationUtils.CHARSET.encode("ko " + throwable.getClass().getName() + "\n"));
					retryOrDie();
				});
		handleExchange();
	}

	private void handleExchange() {
		Logger.debug("Waiting for client message");
		readCommandHandler.startReading();
	}


	@Override
	public void failed(Throwable throwable, AsynchronousServerSocketChannel serverChannel) {
		// TODO handle failure (idk)
	}
}
