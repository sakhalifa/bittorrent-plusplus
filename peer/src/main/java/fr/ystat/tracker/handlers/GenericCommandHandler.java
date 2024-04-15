package fr.ystat.tracker.handlers;

import fr.ystat.commands.ICommand;
import fr.ystat.commands.IReceivableCommand;
import fr.ystat.commands.ISendableCommand;
import fr.ystat.util.SerializationUtils;
import lombok.SneakyThrows;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public final class GenericCommandHandler {

	@SneakyThrows
	public static<T extends IReceivableCommand> void sendCommand(AsynchronousSocketChannel channel,
																 ISendableCommand commandToSend,
																 Class<T> expectedReturnCommandClass,
																 Consumer<T> onFinished,
																 Runnable onFailed){
		channel.write(SerializationUtils.toByteBuffer(commandToSend), channel, new CompletionHandler<>() {
			@Override
			public void completed(Integer bytesWritten, AsynchronousSocketChannel channel) {
				if(bytesWritten == -1){
					onFailed.run();
					return;
				}
				var v = new ReadCommandHandler(channel, (cmd) -> {
					if(!expectedReturnCommandClass.isInstance(cmd)) {
						onFailed.run();
						return;
					}
					onFinished.accept(expectedReturnCommandClass.cast(cmd));
				});
				v.startReading();
			}

			@Override
			public void failed(Throwable throwable, AsynchronousSocketChannel channel) {
				onFailed.run();
			}
		});
	}
}
