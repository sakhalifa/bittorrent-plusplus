package fr.ystat.peer.leecher;

import fr.ystat.Main;
import fr.ystat.files.DownloadedFile;
import fr.ystat.files.StockedFile;
import fr.ystat.handlers.GenericCommandHandler;
import fr.ystat.peer.commands.HaveCommand;
import fr.ystat.peer.commands.InterestedCommand;
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
import java.util.function.Consumer;

public class SeederConnection {
	private final InetSocketAddress seederAddress;
	private final AsynchronousSocketChannel haveChannel;
	private final AsynchronousSocketChannel requestChannel;

	private Thread haveScheduler;

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

	private SeederConnection(InetSocketAddress seederAddress) throws IOException {
		this.seederAddress = seederAddress;
		this.requestChannel = AsynchronousSocketChannel.open();
		this.requestChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		this.haveChannel = AsynchronousSocketChannel.open();
		this.haveChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		Logger.trace("Connecting to {}:{}", this.seederAddress.getHostString(), this.seederAddress.getPort());

		this.requestChannel.connect(this.seederAddress);
		this.haveChannel.connect(this.seederAddress);
	}

	public static SeederConnection newConnection(InetSocketAddress seederAddress) throws IOException {
		var entry = seederConnections.get(seederAddress);

		if (entry == null){
			SeederConnection connection = new SeederConnection(seederAddress);
			seederConnections.put(seederAddress, new ConnectionEntry(connection));
			return connection;
		}
		entry.incrementUsage();
		return entry.connection;
	}

	public void close() throws IOException {
		var entry = seederConnections.get(this.seederAddress);
		if (entry == null){
			// If it is already closed maybe ?
			// I mean just give up already
			return;
		}
		entry.decrementUsage();
		if (entry.connectionAmount == 0){
			seederConnections.remove(this.seederAddress);
			entry.connection.requestChannel.close();
			entry.connection.haveChannel.close();
		}

	}

	public void beginDownload(DownloadedFile file, Consumer<HaveCommand> onEachSuccess, Consumer<Throwable> onEachFailure) throws IOException {
		if (haveScheduler.isAlive()){
			throw new IllegalStateException("Only one download at a time by connection is allowed!");
		}
		haveScheduler = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					// It is not busy waiting, it is fine, interval should be in second time unit
					Thread.sleep(Main.getConfigurationManager().updatePeersIntervalMS());
				} catch (InterruptedException ignored) {
				}
				// launch have update
				HaveCommand hc = new HaveCommand(file);
				GenericCommandHandler.sendCommand(this.haveChannel, hc, HaveCommand.class, onEachSuccess, onEachFailure);

			}
		});
		haveScheduler.start();

	}

	public void endDownload(){
		haveScheduler.interrupt();
	}

	public void sendInterested(InterestedCommand ic, Consumer<HaveCommand> onSuccess, Consumer<Throwable> onFailure){
		GenericCommandHandler.sendCommand(this.requestChannel, ic, HaveCommand.class, onSuccess, onFailure);
	}

	public void sendGetPieces(){

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
