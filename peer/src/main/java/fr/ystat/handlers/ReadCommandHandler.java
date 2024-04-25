package fr.ystat.handlers;

import fr.ystat.Main;
import fr.ystat.commands.CommandAnnotationCollector;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.handlers.exceptions.MaxMessageSizeReachedException;
import fr.ystat.io.exceptions.ConnectionClosedByRemoteException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.SerializationUtils;
import lombok.SneakyThrows;
import org.tinylog.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

public class ReadCommandHandler implements CompletionHandler<Integer, ByteBuffer> {
	public final static int READ_BUFFER_SIZE = 1024;
	private final AsynchronousSocketChannel clientChannel;
	private final StringBuilder messageBuilder;
	private final Consumer<IReceivableCommand> commandConsumer;
	private final Consumer<Throwable> onFailure;
	private long readBytes;


	public ReadCommandHandler(AsynchronousSocketChannel clientChannel, Consumer<IReceivableCommand> commandConsumer, Consumer<Throwable> onFailure) {
		this.clientChannel = clientChannel;
		this.commandConsumer = commandConsumer;
		this.onFailure = onFailure;
		this.messageBuilder = new StringBuilder();

	}

	public void startReading() {
		readBytes = 0;
		ByteBuffer buffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
		clientChannel.read(buffer, buffer, this);
	}


	@SneakyThrows(IOException.class)
	@Override
	public void completed(Integer bytesRead, ByteBuffer buffer) {
		if (bytesRead == -1) {
			this.onFailure.accept(new ConnectionClosedByRemoteException());
			return;
		}
		this.readBytes += bytesRead;
		if (readBytes >= Main.getConfigurationManager().maxMessageSize()) {
			Logger.error("Message exceeded max message size!");
			buffer.clear();
			messageBuilder.setLength(0);
			clientChannel.close();
			this.onFailure.accept(new MaxMessageSizeReachedException());
			return;
		}

		buffer.flip();
		messageBuilder.append(SerializationUtils.CHARSET.decode(buffer));
		buffer.flip();
		if (buffer.get(bytesRead - 1) == '\n') {
			// Finished reading a protocol message
			String wholeMessage = messageBuilder.toString();
			messageBuilder.setLength(0);
			try {
				this.commandConsumer.accept(CommandAnnotationCollector.beginParsing(wholeMessage));
				buffer.clear();
			} catch (ParserException e) {
				this.onFailure.accept(e);
			}
		} else {
			// Still reading that message...
			clientChannel.read(buffer, buffer, this);
		}
	}

	@Override
	public void failed(Throwable throwable, ByteBuffer byteBuffer) {
		this.onFailure.accept(throwable);
	}
}
