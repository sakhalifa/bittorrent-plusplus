package fr.ystat.handlers;

import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.ISendableCommand;
import fr.ystat.commands.exceptions.UnexpectedCommandException;
import fr.ystat.io.exceptions.ChannelClosedByRemoteException;
import fr.ystat.util.SerializationUtils;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

public final class GenericCommandHandler {

	@SneakyThrows
	public static<T extends IReceivableCommand> void sendCommand(AsynchronousSocketChannel channel,
																 ISendableCommand commandToSend,
																 Class<T> expectedReturnCommandClass,
																 Consumer<T> onFinished,
																 Consumer<Throwable> onFailed){
		channel.write(SerializationUtils.toByteBuffer(commandToSend), channel, new CompletionHandler<>() {
			@Override
			public void completed(Integer bytesWritten, AsynchronousSocketChannel channel) {
				if(bytesWritten == -1){
					onFailed.accept(new ChannelClosedByRemoteException());
					return;
				}
				var v = new ReadCommandHandler(channel, (cmd) -> {
					if(!expectedReturnCommandClass.isInstance(cmd)) {
						onFailed.accept(new UnexpectedCommandException("Expected " + expectedReturnCommandClass.getName() + " but got " + cmd.getClass().getName()));
						return;
					}
					onFinished.accept(expectedReturnCommandClass.cast(cmd));
				}, onFailed);
				v.startReading();
			}

			@Override
			public void failed(Throwable throwable, AsynchronousSocketChannel channel) {
				onFailed.accept(throwable);
			}
		});
	}
}
