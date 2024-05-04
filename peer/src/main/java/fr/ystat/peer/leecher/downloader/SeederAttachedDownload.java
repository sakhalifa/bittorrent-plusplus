package fr.ystat.peer.leecher.downloader;

import fr.ystat.Main;
import fr.ystat.files.AtomicBitSet;
import fr.ystat.files.DownloadedFile;
import fr.ystat.files.exceptions.PartitionException;
import fr.ystat.peer.commands.GetPiecesCommand;
import fr.ystat.peer.commands.InterestedCommand;
import fr.ystat.peer.leecher.SeederConnection;
import fr.ystat.peer.leecher.exceptions.FileAlreadyDownloadingException;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SeederAttachedDownload {
    private SeederConnection connection;
    private final AtomicBitSet latestBitSet;

    private final int max_piece_amount_by_message;

    @Getter
    private final DownloadedFile target;

    private final AtomicBitSet reservationBitSet;

    private boolean isDownloadOver = false;

    public SeederAttachedDownload(DownloadedFile target, AtomicBitSet reservationBitSet) {
        this.max_piece_amount_by_message =
                (int) (Main.getConfigurationManager().maxMessageSize() / target.getProperties().getSize());
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

        // This will handle the first answer of haveCommand.
        // We are (mainly) interested in the AtomicBitSet of the seeder. Hence, we should try to trigger
        // tryDownloadABit with the latest version of the seeder's AtomicBitSet.

        InterestedCommand ic = new InterestedCommand(target.getProperties().getHash());
        connection.sendInterested(ic,
                haveCommand -> {
                    synchronized (latestBitSet) {
                        latestBitSet.update(haveCommand.getBitSet());
                    }
                    continueWhileDownloadIsNotOver();
                },
                this::giveUp);
    }

    private void continueWhileDownloadIsNotOver(){
        while (!isDownloadOver){
            progress();
        }
    }

    private void progress() {
        List<Integer> toDownload;
        synchronized (latestBitSet){
            synchronized (reservationBitSet) {
                AtomicBitSet availableData = latestBitSet.andNot(reservationBitSet);
                toDownload = extractValues(availableData.existingIterator().iterator(), max_piece_amount_by_message);
                toDownload.forEach(reservationBitSet::set);  // set the bits as reserved
            }
        }

        if (toDownload.isEmpty()){
            isDownloadOver = true;
            return;
        }

        GetPiecesCommand gpc = new GetPiecesCommand(target.getProperties().getHash(), toDownload);

        connection.sendGetPieces(gpc, dataCommand -> {
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
            progress();

        }, this::giveUp);

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
    }
}
