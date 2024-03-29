package fr.ystat.server.handler;

import fr.ystat.commands.ICommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.commands.server.CommandAnnotationCollector;
import fr.ystat.config.IConfigurationManager;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.server.Counter;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class ExecuteCommandHandler implements CompletionHandler<Integer, Void> {
	private final AsynchronousSocketChannel clientChannel;
	private final ReadCommandHandler readHandler;
	private final Counter counter;
	private final IConfigurationManager configurationManager;

	public ExecuteCommandHandler(AsynchronousSocketChannel clientChannel, Counter counter, IConfigurationManager configurationManager){
		this.clientChannel = clientChannel;
		this.counter = counter;
		this.readHandler = new ReadCommandHandler(clientChannel, this, configurationManager);
		this.configurationManager = configurationManager;
	}

	ExecuteCommandHandler(AsynchronousSocketChannel clientChannel, ReadCommandHandler readHandler, Counter counter, IConfigurationManager configurationManager){
		this.clientChannel = clientChannel;
		this.readHandler = readHandler;
		this.counter = counter;
		this.configurationManager = configurationManager;
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
			ICommand command = CommandAnnotationCollector.beginParsing(input);
			String response = command.apply(counter);
			clientChannel.write(StandardCharsets.ISO_8859_1.encode(response + "\n"), null, this);
		} catch (ParserException e) {
			ByteBuffer toWrite = StandardCharsets.ISO_8859_1.encode("PARSER ERROR: " + e.getMessage() + "\n");
			System.out.println("Writing " + toWrite.capacity() + " bytes");
			clientChannel.write(toWrite, null, this);
		} catch (CommandException e) {
			clientChannel.write(StandardCharsets.ISO_8859_1.encode("COMMAND ERROR: " + e.getMessage() + "\n"), null, this);

		}
	}
}
