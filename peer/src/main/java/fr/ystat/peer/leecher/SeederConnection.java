package fr.ystat.peer.leecher;

import fr.ystat.Main;
import fr.ystat.handlers.GenericCommandHandler;
import fr.ystat.peer.commands.HaveCommand;
import fr.ystat.peer.leecher.downloader.SeederAttachedDownload;
import lombok.Getter;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SeederConnection {
    @Getter
    private final InetSocketAddress seederAddress;
    private final AsynchronousSocketChannel haveChannel;

    static final private ConcurrentHashMap<InetSocketAddress, SeederConnection> seederConnections = new ConcurrentHashMap<>();

    private final Set<SeederAttachedDownload> downloads = ConcurrentHashMap.newKeySet();

    private SeederConnection(InetSocketAddress seederAddress) throws IOException {
        this.seederAddress = seederAddress;

        this.haveChannel = AsynchronousSocketChannel.open();
        this.haveChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        Logger.trace("[HAVE CHANNEL] Connecting to {}:{}", this.seederAddress.getHostString(), this.seederAddress.getPort());

        this.haveChannel.connect(this.seederAddress, null, new CompletionHandler<Void, Void>() {

            @Override
            public void completed(Void result, Void attachment) {
                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(Main.getConfigurationManager().updatePeersIntervalMS());
                        } catch (InterruptedException ignored) {
                        }
                        notifySeeders();
                    }
                }).start();
            }

            @Override
            public void failed(Throwable exc, Void attachment) {

            }
        });
    }

    public static SeederConnection newConnection(InetSocketAddress seederAddress, SeederAttachedDownload download) throws IOException {
        SeederConnection connection = seederConnections.get(seederAddress);
        if (connection == null) {
            // TODO : maybe prevent new connection from being established if we are already saturated
            // Semaphore time ? :D
            connection = new SeederConnection(seederAddress);
            seederConnections.put(seederAddress, connection);
            return connection;
        }

        synchronized (seederConnections.get(seederAddress)) {
            seederConnections.get(seederAddress).downloads.add(download);
            Logger.trace("Adding download");
        }
        Logger.trace("Established new connection to " + seederAddress.getHostName());
        return connection;
    }

    public void close(SeederAttachedDownload download, Consumer<Throwable> onFailure) {
        try {
            close(download);
        } catch (IOException e) {
            onFailure.accept(e);
        }
    }

    public void close(SeederAttachedDownload download) throws IOException {

        download.close();
		downloads.remove(download);
         if (downloads.isEmpty()) {
             synchronized (this) {
                 Logger.trace("Closing connection to " + this.seederAddress.getHostName());
                 haveChannel.close();
             }
            seederConnections.remove(this.seederAddress);
        }
    }

    private void notifySeeders() {
        // For each download we are currently doing, get our most recent bitset of the partitions of the local file
        // and send it happily to our seeders
        Logger.trace("Notifying seeder");
        downloads.forEach(it -> {
            HaveCommand hc = new HaveCommand(it.getTarget().getProperties().getHash(), it.getTarget().getBitSet());
            Logger.trace("Sending have for download {}", it.getTarget().getProperties().getName());
            sendHave(hc, haveCommand -> {
                // Update local download latestBitSets
                it.updateLatestBitSet(haveCommand.getBitSet());
            }, throwable -> {
            });
        });
    }

    public void sendHave(HaveCommand hc, Consumer<HaveCommand> onSuccess, Consumer<Throwable> onFailure) {
        GenericCommandHandler.sendCommand(this.haveChannel, hc, HaveCommand.class, onSuccess, onFailure);
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
