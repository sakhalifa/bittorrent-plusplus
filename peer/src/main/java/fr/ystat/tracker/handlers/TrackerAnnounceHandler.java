package fr.ystat.tracker.handlers;

import fr.ystat.util.Pair;
import fr.ystat.util.SerializationUtils;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class TrackerAnnounceHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
	@SneakyThrows
	@Override
	public void completed(Integer bytesWritten, AsynchronousSocketChannel channel) {
		if(bytesWritten == -1){
			System.err.println("Couldn't write to channel. Exiting");
			channel.close();
			System.exit(1);
		}
		var buf = ByteBuffer.allocate(4);
		channel.read(buf, new Pair<>(channel, buf), new CompletionHandler<>() {
			@SneakyThrows
			@Override
			public void completed(Integer bytesRead, Pair<AsynchronousSocketChannel, ByteBuffer> pair) {
				pair.getSecond().flip();
				if(!SerializationUtils.CHARSET.decode(pair.getSecond()).toString().trim().equals("ok")){
					System.err.println("Announce failed. Exiting");
					pair.getFirst().close();
					System.exit(1);
				}
			}

			@SneakyThrows
			@Override
			public void failed(Throwable throwable, Pair<AsynchronousSocketChannel, ByteBuffer> pair) {
				System.err.println("Couldn't receive announce result from tracker. Exiting");
				pair.getFirst().close();
				System.exit(1);
			}
		});
	}

	@SneakyThrows
	@Override
	public void failed(Throwable throwable, AsynchronousSocketChannel asynchronousSocketChannel) {
		System.err.println("Announce failed. Exiting");
		asynchronousSocketChannel.close();
		System.exit(1);
	}
}
