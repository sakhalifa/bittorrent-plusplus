package fr.ystat.peer.seeder.handlers;

import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.commands.CommandAnnotationCollector;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.peer.Counter;
import fr.ystat.util.SerializationUtils;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class ExecuteCommandHandler implements CompletionHandler<Integer, Void> {
	private final AsynchronousSocketChannel clientChannel;
	private final ReadCommandHandler readHandler;
	private final Counter counter;

	public ExecuteCommandHandler(AsynchronousSocketChannel clientChannel, Counter counter){
		this.clientChannel = clientChannel;
		this.counter = counter;
		this.readHandler = new ReadCommandHandler(clientChannel, this);
	}

	ExecuteCommandHandler(AsynchronousSocketChannel clientChannel, ReadCommandHandler readHandler, Counter counter){
		this.clientChannel = clientChannel;
		this.readHandler = readHandler;
		this.counter = counter;
	}

	@Override
	public void completed(Integer bytesWritten, Void unused) {
		System.out.println("Wrote " + bytesWritten + " bytes");
		readHandler.startReading();
	}

	@Override
	public void failed(Throwable throwable, Void unused) {

	}

	public void execute(String input) {
		try {
			IReceivableCommand command = CommandAnnotationCollector.beginParsing(input);
			String response = command.apply();
			clientChannel.write(SerializationUtils.CHARSET.encode(response + "\n"), null, this);
		} catch (ParserException e) {
			ByteBuffer toWrite = SerializationUtils.CHARSET.encode("PARSER ERROR: " + e.getMessage() + "\n");
			System.out.println("Writing " + toWrite.capacity() + " bytes");
			clientChannel.write(toWrite, null, this);
		} catch (CommandException e) {
			clientChannel.write(SerializationUtils.CHARSET.encode("COMMAND ERROR: " + e.getMessage() + "\n"), null, this);

		}
	}
}
