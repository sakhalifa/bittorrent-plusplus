package fr.ystat.peer.leecher;

import fr.ystat.Main;
import fr.ystat.peer.seeder.Seeder;
import fr.ystat.tracker.handlers.TrackerConnectionHandler;
import fr.ystat.util.Pair;
import lombok.Getter;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SeederConnection {
	private final InetSocketAddress seederAddress;
	private final AsynchronousSocketChannel haveChannel;
	private final AsynchronousSocketChannel requestChannel;

	static class ConnectionEntry {
		@Getter
		private final SeederConnection connection;
		private int connectionAmount = 1;


        ConnectionEntry(SeederConnection connection) {
            this.connection = connection;
        }


		public void incrementUsage(){
			connectionAmount += 1;
		}

		public void decrementUsage(){
			connectionAmount -= 1;
		}
    }

	// Value is : connection to amount of time that the connection is currently used
	static final private HashMap<InetSocketAddress, ConnectionEntry> seederConnections = new HashMap<>();

	public static SeederConnection newConnection(InetSocketAddress seederAddress) throws IOException {
		var entry = seederConnections.get(seederAddress);

		if (entry == null){
			SeederConnection connection = new SeederConnection(seederAddress);
			seederConnections.put(seederAddress, new Pair<>(connection, 1));
			return connection;
		}
		seederConnections.replace(seederAddress, new Pair<>(entry.getFirst(), entry.getSecond() + 1));
		return entry.getFirst();
	}

	public void close() {
		var entry = seederConnections.get(this.seederAddress);
		if (entry.getSecond()
	}


	private SeederConnection(InetSocketAddress seederAddress) throws IOException {
		this.seederAddress = seederAddress;
		this.requestChannel = AsynchronousSocketChannel.open();
		this.requestChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		this.haveChannel = AsynchronousSocketChannel.open();
		this.haveChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		Logger.trace("Connecting to {}:{}", this.seederAddress.getHostString(), this.seederAddress.getPort());

		this.requestChannel.connect(this.seederAddress, null, completion handler);
		this.haveChannel.connect(this.seederAddress, null,
				new CompletionHandler<Void, Void>() {
					@Override
					public void completed(Void unused, Void unused2) {
						// Triggered Once connected
						new Thread(() -> {
							try {
								Thread.sleep(Main.getConfigurationManager().updatePeersIntervalMS());
							} catch (InterruptedException ignored) {
							}
							// Launch have at regular intervals
						}).start();
					}

					@Override
					public void failed(Throwable throwable, Void unused) {
						Logger.error(throwable, String.format("Could not connect to peer %s !", seederAddress.getHostString()));
						System.exit(1);
					}
				}
		);
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SeederConnection that = (SeederConnection) o;
		return Objects.equals(seederAddress, that.seederAddress);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(seederAddress);
	}


}
