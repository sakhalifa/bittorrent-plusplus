package fr.ystat.peer.leecher.downloader;

import fr.ystat.Main;
import fr.ystat.files.AtomicBitSet;
import fr.ystat.files.DownloadedFile;
import fr.ystat.peer.leecher.SeederConnection;
import fr.ystat.peer.leecher.exceptions.DownloadException;
import fr.ystat.tracker.commands.client.PeersCommand;
import fr.ystat.tracker.commands.server.GetFileCommand;

import java.io.IOException;

public class GreedyDownloader extends FileDownloader {

    public GreedyDownloader(DownloadedFile target) {
        super(target);
        reservationBitSet = target.getBitSet();
    }

    private final AtomicBitSet reservationBitSet;

    @Override
    public void startDownload() throws DownloadException {
        sendTrackerFileRequest();
    }

    public void sendTrackerFileRequest(){
        GetFileCommand gfc = new GetFileCommand(target.getProperties().getHash());
        Main.getTrackerConnection().sendGetFile(gfc, this::askPeers, this::giveUp);
    }

    public void askPeers(PeersCommand pc){
        pc.getPeers().parallelStream().forEach(
            it -> {
                SeederConnection connection = null;
                SeederAttachedDownload download = null;
                try {
                    download = new SeederAttachedDownload(target, reservationBitSet);

                    // This step could be blocking if seeder connections are saturated
                    connection = SeederConnection.newConnection(it, download);

                    // This bad boy will work his way out of it.
                    download.startDownloadOnSeeder(connection);

                    connection.close(download);

                } catch (IOException | DownloadException e) {
                    // If we fail to connect retry X amount of time or just go to the next peer
                    this.giveUp(e);
                } finally {
                    // download != null could be removed but for sanity reasons let's keep it :)
                    if (connection != null && download != null) {
                        connection.close(download, this::giveUp);
                    }
                }
            }
        );
    }

    private void giveUp(Throwable t) {
        // it's over sadly, you did your best
    }
}
