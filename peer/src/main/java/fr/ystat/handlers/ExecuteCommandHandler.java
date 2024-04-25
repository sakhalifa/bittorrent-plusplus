package fr.ystat.handlers;

import fr.ystat.commands.CommandAnnotationCollector;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.parser.exceptions.ParserException;
import fr.ystat.util.SerializationUtils;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

public class ExecuteCommandHandler implements CompletionHandler<Integer, Void> {
	private final AsynchronousSocketChannel clientChannel;
	private Throwable failureThrowable;
	private final Runnable onSuccess;
	private final Consumer<Throwable> onFailure;

	public ExecuteCommandHandler(AsynchronousSocketChannel clientChannel, Runnable onSuccess, Consumer<Throwable> onFailure){
		this.clientChannel = clientChannel;
		this.onSuccess = onSuccess;
		this.onFailure = onFailure;
		failureThrowable = null;
	}

	@Override
	public void completed(Integer bytesWritten, Void unused) {
		if(failureThrowable != null)
			onFailure.accept(failureThrowable);
		else
			onSuccess.run();
		failureThrowable = null;
	}

	@Override
	public void failed(Throwable throwable, Void unused) {
		if(failureThrowable != null)
			failureThrowable.initCause(throwable);
		else
			failureThrowable = throwable;
		onFailure.accept(failureThrowable);
		failureThrowable = null;
	}

	public void execute(IReceivableCommand command) {
		try {
			String response = command.apply();
			clientChannel.write(SerializationUtils.CHARSET.encode(response + "\n"), null, this);
		}catch (CommandException e) {
			failureThrowable = e;
			clientChannel.write(SerializationUtils.CHARSET.encode("ko " + e.getClass().getName() + "\n"), null, this);
		}
	}
}
