package fr.ystat.tracker.handlers;

import fr.ystat.commands.CommandAnnotationCollector;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.config.GlobalConfiguration;
import fr.ystat.server.Counter;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import static fr.ystat.server.handler.ConnectionHandler.BUFFER_SIZE;

public class ReadCommandHandler implements CompletionHandler<Integer, ByteBuffer> {
	private final AsynchronousSocketChannel clientChannel;
	private final StringBuilder messageBuilder;
	private final Consumer<IReceivableCommand> commandConsumer;
	private long readBytes;


	public ReadCommandHandler(AsynchronousSocketChannel clientChannel, Consumer<IReceivableCommand> commandConsumer) {
		this.clientChannel = clientChannel;
		this.commandConsumer = commandConsumer;
		this.messageBuilder = new StringBuilder();

	}

	public void startReading(){
		readBytes = 0;
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		clientChannel.read(buffer, buffer, this);
	}


	@SneakyThrows
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
		messageBuilder.append(StandardCharsets.ISO_8859_1.decode(buffer));
		buffer.flip();
		if(buffer.get(bytesRead - 1) == '\n'){
			// Finished reading a protocol message
			String wholeMessage = messageBuilder.toString();
			messageBuilder.setLength(0);
			System.out.println("Read the message :'" + wholeMessage.trim() + "'");
			this.commandConsumer.accept(CommandAnnotationCollector.beginParsing(wholeMessage));
			buffer.clear();
		}else{
			// Still reading that message...
			clientChannel.read(buffer, buffer, this);
		}
	}

	@Override
	public void failed(Throwable throwable, ByteBuffer byteBuffer) {

	}
}
