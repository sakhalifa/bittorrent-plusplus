package fr.ystat.handlers;

import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.io.exceptions.ConnectionClosedByRemoteException;
import fr.ystat.util.SerializationUtils;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

public class ExecuteCommandHandler implements CompletionHandler<Integer, Void> {
	public final static int WRITE_BUFFER_SIZE = 2048;
	private final AsynchronousSocketChannel clientChannel;
	private Throwable failureThrowable;
	private final Runnable onSuccess;
	private final Consumer<Throwable> onFailure;
	private ByteBuffer toWrite;
	private int trueLimit;

	public ExecuteCommandHandler(AsynchronousSocketChannel clientChannel, Runnable onSuccess, Consumer<Throwable> onFailure){
		this.clientChannel = clientChannel;
		this.onSuccess = onSuccess;
		this.onFailure = onFailure;
		failureThrowable = null;
	}

	private void resetInternalWriteState(){
		trueLimit = 0;
		toWrite = null;
	}

	@Override
	public void completed(Integer bytesWritten, Void unused) {
		if(bytesWritten == -1){
			onFailure.accept(new ConnectionClosedByRemoteException());
			return;
		}
		if (failureThrowable != null) {
			resetInternalWriteState();
			onFailure.accept(failureThrowable);
			failureThrowable = null;
			return;
		}

		if(toWrite.limit() == trueLimit) {
			resetInternalWriteState();
			onSuccess.run();
			return;
		}
		toWrite.limit(Math.min(toWrite.limit() + WRITE_BUFFER_SIZE, trueLimit));
		clientChannel.write(toWrite, null, this);
	}

	@Override
	public void failed(Throwable throwable, Void unused) {
		resetInternalWriteState();
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
			toWrite = SerializationUtils.CHARSET.encode(response + "\n");
			trueLimit = toWrite.limit();
			toWrite = toWrite.limit(Math.min(WRITE_BUFFER_SIZE, trueLimit));
			clientChannel.write(toWrite, null, this);
		}catch (CommandException e) {
			failureThrowable = e;
			// Safe to ignore WRITE_BUFFER, we know it won't ever overflow come one now
			clientChannel.write(SerializationUtils.CHARSET.encode("ko " + e.getClass().getName() + "\n"), null, this);
		}
	}
}
