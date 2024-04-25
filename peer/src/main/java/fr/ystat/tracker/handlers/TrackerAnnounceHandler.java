package fr.ystat.tracker.handlers;

import fr.ystat.util.Pair;
import fr.ystat.util.SerializationUtils;
import lombok.SneakyThrows;
import org.tinylog.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class TrackerAnnounceHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
	@SneakyThrows
	@Override
	public void completed(Integer bytesWritten, AsynchronousSocketChannel trackerChannel) {
		if(bytesWritten == -1){
			Logger.error("Couldn't write to channel. Exiting");
			trackerChannel.close();
			System.exit(1);
		}
		var buf = ByteBuffer.allocate(4);
		trackerChannel.read(buf, new Pair<>(trackerChannel, buf), new CompletionHandler<>() {
			@SneakyThrows
			@Override
			public void completed(Integer bytesRead, Pair<AsynchronousSocketChannel, ByteBuffer> pair) {
				if(bytesRead == -1){
					Logger.error("Tracker connection terminated. Exiting");
					trackerChannel.close();
					System.exit(1);
				}
				pair.getSecond().flip();
				if(!SerializationUtils.CHARSET.decode(pair.getSecond()).toString().trim().equals("ok")){
					Logger.error("Announce failed. Exiting");
					pair.getFirst().close();
					System.exit(1);
				}
			}

			@SneakyThrows
			@Override
			public void failed(Throwable throwable, Pair<AsynchronousSocketChannel, ByteBuffer> pair) {
				Logger.error("Couldn't receive announce result from tracker. Exiting");
				pair.getFirst().close();
				System.exit(1);
			}
		});
	}

	@SneakyThrows
	@Override
	public void failed(Throwable throwable, AsynchronousSocketChannel trackerChannel) {
		Logger.error("Announce failed. Exiting");
		trackerChannel.close();
		System.exit(1);
	}
}
