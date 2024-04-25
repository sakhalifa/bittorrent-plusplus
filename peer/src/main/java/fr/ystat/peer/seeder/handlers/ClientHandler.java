package fr.ystat.peer.seeder.handlers;

import fr.ystat.Main;
import fr.ystat.handlers.ExecuteCommandHandler;
import fr.ystat.handlers.ReadCommandHandler;
import fr.ystat.io.exceptions.ChannelClosedByRemoteException;
import fr.ystat.util.SerializationUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler implements CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel> {

	private final AtomicInteger numberOfConnections;
	private ReadCommandHandler readCommandHandler;
	private ExecuteCommandHandler executeCommandHandler;
	private AsynchronousSocketChannel clientChannel;
	private int failures = 1;

	public ClientHandler(AtomicInteger numberOfConnections) {
		this.numberOfConnections = numberOfConnections;
	}


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
		serverChannel.accept(serverChannel, new ClientHandler(numberOfConnections));
		Logger.debug(numberOfConnections.get());
		Logger.debug(Main.getConfigurationManager().maxLeechers());
		if(numberOfConnections.getAndIncrement() >= Main.getConfigurationManager().maxLeechers()) {
			Logger.trace("Max leechers reached. Closing...");
			numberOfConnections.decrementAndGet();
			try {
				clientChannel.close();
				return;
			} catch (IOException ignored) {
				Logger.error(ignored);
				return;
			}
		}
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
					if(throwable instanceof ChannelClosedByRemoteException) {
						try {
							Logger.debug("Client {} disconnected", clientChannel.getRemoteAddress());
							clientChannel.close();
							numberOfConnections.decrementAndGet();
							return;
						} catch (IOException e) {
							Logger.error(e);
							return;
						}
					}
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
		Logger.debug("Failure???");
	}
}
