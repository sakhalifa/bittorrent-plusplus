package fr.ystat.peer.seeder.handlers;

import fr.ystat.Main;
import fr.ystat.peer.Counter;
import fr.ystat.util.SerializationUtils;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import static fr.ystat.handlers.ConnectionHandler.BUFFER_SIZE;

public class ReadCommandHandler implements CompletionHandler<Integer, ByteBuffer> {
	private final AsynchronousSocketChannel clientChannel;
	private final StringBuilder messageBuilder;
	private final ExecuteCommandHandler commandHandler;
	private long readBytes;


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

	public void startReading(){
		readBytes = 0;
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		clientChannel.read(buffer, buffer, this);
	}

	void startReading(ByteBuffer allocatedBuf){
		readBytes = 0;
		clientChannel.read(allocatedBuf, allocatedBuf, this);
	}


	@SneakyThrows
	@Override
	public void completed(Integer bytesRead, ByteBuffer buffer) {
		if(bytesRead == -1)
			return;
		System.out.printf("[%s] ReadCommandHandler on Thread %d%n", clientChannel.getRemoteAddress(), Thread.currentThread().getId());
		this.readBytes += bytesRead;
		if(readBytes >= Main.getConfigurationManager().maxMessageSize()){
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
