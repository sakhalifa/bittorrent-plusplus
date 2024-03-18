package fr.ystat.server.handler;

import fr.ystat.server.Counter;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class ReadCommandHandler implements CompletionHandler<Integer, ByteBuffer> {
	private final AsynchronousSocketChannel clientChannel;
	private final StringBuilder messageBuilder;
	private final ExecuteCommandHandler commandHandler;


	public ReadCommandHandler(AsynchronousSocketChannel clientChannel, Counter counter) {
		this.clientChannel = clientChannel;
		this.messageBuilder = new StringBuilder();
		this.commandHandler = new ExecuteCommandHandler(clientChannel, this, counter);
	}

	ReadCommandHandler(AsynchronousSocketChannel clientChannel, ExecuteCommandHandler commandHandler){
		this.clientChannel = clientChannel;
		this.commandHandler = commandHandler;
		this.messageBuilder = new StringBuilder();
	}


	@Override
	public void completed(Integer bytesRead, ByteBuffer buffer) {
		if(bytesRead == -1)
			return;

		System.out.println("Called read handler");
		System.out.println("Read " + bytesRead + " bytes!");
		buffer.flip();
		messageBuilder.append(StandardCharsets.ISO_8859_1.decode(buffer));
		buffer.flip();
		if(buffer.get(bytesRead - 1) == '\n'){
			// Finished reading a protocol message
			String wholeMessage = messageBuilder.toString();
			messageBuilder.setLength(0);
			System.out.println("Read the message :'" + wholeMessage.trim() + "'");
			this.commandHandler.execute(wholeMessage);
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
