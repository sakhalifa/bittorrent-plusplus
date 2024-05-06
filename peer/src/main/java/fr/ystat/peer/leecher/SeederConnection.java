package fr.ystat.peer.leecher;

import fr.ystat.Main;
import fr.ystat.handlers.GenericCommandHandler;
import fr.ystat.peer.commands.DataCommand;
import fr.ystat.peer.commands.GetPiecesCommand;
import fr.ystat.peer.commands.HaveCommand;
import fr.ystat.peer.commands.InterestedCommand;
import fr.ystat.peer.leecher.downloader.SeederAttachedDownload;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class SeederConnection {
    private final InetSocketAddress seederAddress;
    private final AsynchronousSocketChannel haveChannel;
    private final Future<Void> requestChannelFuture;

    static final private ConcurrentHashMap<InetSocketAddress, SeederConnection> seederConnections = new ConcurrentHashMap<>();

    private final Set<SeederAttachedDownload> downloads = ConcurrentHashMap.newKeySet();


    private SeederConnection(InetSocketAddress seederAddress) throws IOException {
        this.seederAddress = seederAddress;
        this.requestChannel = AsynchronousSocketChannel.open();
        this.requestChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        this.haveChannel = AsynchronousSocketChannel.open();
        this.haveChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

        Logger.trace("Connecting to {}:{}", this.seederAddress.getHostString(), this.seederAddress.getPort());

        this.requestChannelFuture = this.requestChannel.connect(this.seederAddress);

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

    private void ensureChannelAreReady(Consumer<Throwable> onFailure) {
        if (this.requestChannelFuture.isDone()) return;
        synchronized (this.requestChannelFuture) {
            if (this.requestChannelFuture.isDone()) return;
            try {
                this.requestChannelFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                onFailure.accept(e);
            }
        }
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
        }

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
		downloads.remove(download);
         if (downloads.isEmpty()) {
             synchronized (this) {
                 requestChannel.close();
                 haveChannel.close();
             }
            seederConnections.remove(this.seederAddress);
        }
    }

    private void notifySeeders() {
        // For each download we are currently doing, get our most recent bitset of the partitions of the local file
        // and send it happily to our seeders
        downloads.forEach(it -> {
            HaveCommand hc = new HaveCommand(it.getTarget().getProperties().getHash(), it.getTarget().getBitSet());
            sendHave(hc, haveCommand -> {
                // Update local download latestBitSets
                it.updateLatestBitSet(haveCommand.getBitSet());
            }, throwable -> {
            });
        });
    }

    public void sendInterested(InterestedCommand ic, Consumer<HaveCommand> onSuccess, Consumer<Throwable> onFailure) {
        ensureChannelAreReady(onFailure);
        GenericCommandHandler.sendCommand(this.requestChannel, ic, HaveCommand.class, onSuccess, onFailure);
    }

    public void sendGetPieces(GetPiecesCommand gpc, Consumer<DataCommand> onSuccess, Consumer<Throwable> onFailure) {
        ensureChannelAreReady(onFailure);
        GenericCommandHandler.sendCommand(this.requestChannel, gpc, DataCommand.class, onSuccess, onFailure);
    }

    public void sendHave(HaveCommand hc, Consumer<HaveCommand> onSuccess, Consumer<Throwable> onFailure) {
        ensureChannelAreReady(onFailure);
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
