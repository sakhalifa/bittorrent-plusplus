package fr.ystat.peer.leecher.downloader;

import fr.ystat.Main;
import fr.ystat.files.AtomicBitSet;
import fr.ystat.files.DownloadedFile;
import fr.ystat.files.exceptions.PartitionException;
import fr.ystat.handlers.GenericCommandHandler;
import fr.ystat.peer.commands.DataCommand;
import fr.ystat.peer.commands.GetPiecesCommand;
import fr.ystat.peer.commands.HaveCommand;
import fr.ystat.peer.commands.InterestedCommand;
import fr.ystat.peer.leecher.SeederConnection;
import fr.ystat.peer.leecher.exceptions.FileAlreadyDownloadingException;
import lombok.Getter;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class SeederAttachedDownload {
    private SeederConnection connection;
    private final AtomicBitSet latestBitSet;

    private final int max_piece_amount_by_message;


    // Play with this value if you feel like it.
    private final static int estimated_message_header = 1024;

    private final AsynchronousSocketChannel requestChannel;

    @Getter
    private final DownloadedFile target;

    private final AtomicBitSet reservationBitSet;

    public SeederAttachedDownload(DownloadedFile target, AtomicBitSet reservationBitSet) throws IOException {

        this.requestChannel = AsynchronousSocketChannel.open();
        this.requestChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

//        this.max_piece_amount_by_message =
//                (int) (Main.getConfigurationManager().maxMessageSize() - estimated_message_header) / target.getProperties().getSize());
        this.max_piece_amount_by_message = 1;
        Logger.trace("Max piece amount by message {}", max_piece_amount_by_message);
        this.target = target;
        this.reservationBitSet = reservationBitSet;
        latestBitSet = target.getBitSet();
        latestBitSet.empty();
    }

    public void updateLatestBitSet(AtomicBitSet updatedBitSet) {
        synchronized (latestBitSet) {
            latestBitSet.update(updatedBitSet);
        }
    }

    public void startDownloadOnSeeder(SeederConnection connection) throws FileAlreadyDownloadingException {
        if (connection == null) throw new IllegalArgumentException("connection cannot be null");

        if (this.connection != null) throw new FileAlreadyDownloadingException();
        this.connection = connection;
        Logger.trace("[REQUEST CHANNEL][file: {}] Connecting to {}:{}",
                this.target.getProperties().getName(),
                this.connection.getSeederAddress().getHostString(),
                this.connection.getSeederAddress().getPort());
        this.requestChannel.connect(connection.getSeederAddress(), null, new CompletionHandler<Void, Void>() {
            @Override
            public void completed(Void result, Void attachment) {
                InterestedCommand ic = new InterestedCommand(target.getProperties().getHash());
                sendInterested(ic,
                        haveCommand -> {
                            synchronized (latestBitSet) {
                                latestBitSet.update(haveCommand.getBitSet());
                                Logger.trace("Updating latest bitset {}", latestBitSet);
                            }
                            try {
                                progress();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        (t) -> {});
            }

            @Override
            public void failed(Throwable exc, Void attachment) {}
        });

        // This will handle the first answer of haveCommand.
        // We are (mainly) interested in the AtomicBitSet of the seeder. Hence, we should try to trigger
        // tryDownloadABit with the latest version of the seeder's AtomicBitSet.

    }

    public void sendInterested(InterestedCommand ic, Consumer<HaveCommand> onSuccess, Consumer<Throwable> onFailure) {
        GenericCommandHandler.sendCommand(this.requestChannel, ic, HaveCommand.class, onSuccess, onFailure);
    }

    public void sendGetPieces(GetPiecesCommand gpc, Consumer<DataCommand> onSuccess, Consumer<Throwable> onFailure) {
        GenericCommandHandler.sendCommand(this.requestChannel, gpc, DataCommand.class, onSuccess, onFailure);
    }

    private void progress() throws InterruptedException {
        List<Integer> toDownload;
        synchronized (latestBitSet){
            synchronized (reservationBitSet) {
                AtomicBitSet availableData = latestBitSet.andNot(reservationBitSet);
                toDownload = extractValues(availableData.existingIterator().iterator(), max_piece_amount_by_message);
                toDownload.forEach(reservationBitSet::set);  // set the bits as reserved
            }
        }
        Logger.trace("Scheduling download of {}", toDownload.stream().map(Object::toString).collect(Collectors.joining(", ")));

        if (toDownload.isEmpty()){
            if (reservationBitSet.isFilled()){
                Logger.trace("Nothing more to download, closing attachedDownload");
                connection.close(this, this::giveUp);
                return;
            }
            Logger.trace("Waiting a bit to resume download");
            Thread.sleep(1000);
            progress();

        }

        GetPiecesCommand gpc = new GetPiecesCommand(target.getProperties().getHash(), toDownload);

        sendGetPieces(gpc, dataCommand -> {
            dataCommand.getDataMap().forEach(
                    (index, data) -> {
                        // If we add a partition, and it yields an error.
                        // Should we :
                        // Mark the index as not valid, and queue it for download again ? (set it as false in the reservationBitSet)
                        // Try again, maybe the error was IOException from writeFile, and downloading it again won't help.
                        // ?
                        try {
                            target.addPartition(index, data.array());
                        } catch (PartitionException ignored) {
                            // if the partition is already present we are good to go
                        } catch (IOException e) {
                            // try again ? one time maybe ? sleep a bit ?
                            target.addPartition(index, data.array(), this::giveUp);
                            // log or throw the error up I guess

                        }
                    }
            );
            // now we want to loop back to see what else we can fetch.
            try {
                progress();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }, this::giveUp);

    }

    public void close() throws IOException {
        Logger.trace("Stopping seederAttachedDownload of " + this.getTarget().getProperties().getName());
        this.requestChannel.close();
    }

    private List<Integer> extractValues(Iterator<Long> iterator, int N){
        List<Integer> values = new ArrayList<>();
        while (iterator.hasNext() && N > 0){
            values.add(Math.toIntExact(iterator.next()));  // should hopefully never overflow :D
            N -= 1;
        }
        return values;
    }

    private void giveUp(Throwable t) {
        // it's over sadly, you did your best
        Logger.trace("Giving up on seeder attached download, got {}", t);
    }
}
