package fr.ystat.handlers;

import fr.ystat.commands.CommandAnnotationCollector;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.config.GlobalConfiguration;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.SerializationUtils;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static fr.ystat.handlers.ConnectionHandler.BUFFER_SIZE;

public class ReadCommandHandler implements CompletionHandler<Integer, ByteBuffer> {
	private final AsynchronousSocketChannel clientChannel;
	private final StringBuilder messageBuilder;
	private final Consumer<IReceivableCommand> commandConsumer;
	private final Runnable onFailure;
	private long readBytes;


	public ReadCommandHandler(AsynchronousSocketChannel clientChannel, Consumer<IReceivableCommand> commandConsumer, Runnable onFailure) {
		this.clientChannel = clientChannel;
		this.commandConsumer = commandConsumer;
		this.onFailure = onFailure;
		this.messageBuilder = new StringBuilder();

	}

	public void startReading(){
		readBytes = 0;
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		clientChannel.read(buffer, buffer, this);
	}


	@SneakyThrows(IOException.class)
	@Override
	public void completed(Integer bytesRead, ByteBuffer buffer) {
		if(bytesRead == -1)
			return;
		this.readBytes += bytesRead;
		if(readBytes >= GlobalConfiguration.get().getMaxMessageSize()){
			System.err.println("Message exceeded max message size.");
			buffer.clear();
			messageBuilder.setLength(0);
			clientChannel.close();
			return;
		}

		buffer.flip();
		messageBuilder.append(SerializationUtils.CHARSET.decode(buffer));
		buffer.flip();
		if(buffer.get(bytesRead - 1) == '\n'){
			// Finished reading a protocol message
			String wholeMessage = messageBuilder.toString();
			messageBuilder.setLength(0);
			System.out.println("Read the message :'" + wholeMessage.trim() + "'");
			try {
				this.commandConsumer.accept(CommandAnnotationCollector.beginParsing(wholeMessage));
				buffer.clear();
			} catch (ParserException e) {
				this.onFailure.run();
			}
		}else{
			// Still reading that message...
			clientChannel.read(buffer, buffer, this);
		}
	}

	@Override
	public void failed(Throwable throwable, ByteBuffer byteBuffer) {

	}
}
