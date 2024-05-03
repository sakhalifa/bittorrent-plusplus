package fr.ystat.files.downloader;

import fr.ystat.Main;
import fr.ystat.files.FileInventory;
import fr.ystat.files.StockedFile;
import fr.ystat.peer.commands.GetPiecesCommand;
import fr.ystat.peer.commands.InterestedCommand;
import fr.ystat.tracker.commands.client.PeersCommand;
import fr.ystat.tracker.commands.server.GetFileCommand;

import java.net.InetSocketAddress;

public class GreedyDownloader extends FileDownloader {

    public GreedyDownloader(String targetHash) {
        super(targetHash);
    }

    @Override
    void startDownload() {
        StockedFile localVersion = FileInventory.getInstance().getStockedFile(super.targetHash);
        if (null == localVersion){
            // Should not happen as our GUI does not allow it, however, for more complete implementation, you may
            // want to do the following steps
            // Retrieve the file info from the tracker
            // Send a look command.

            throw new RuntimeException("Downloading without local file information is not yet allowed");
        }
    }

    public void giveUp(Throwable t) {
        // it's over sadly, you did your best
    }

    public void sendTrackerFileRequest(){
        GetFileCommand gfc = new GetFileCommand(super.targetHash);
        Main.getTrackerConnection().sendGetFile(gfc, this::downloadFromPeers, this::giveUp);
    }

    public void downloadFromPeers(PeersCommand pc){
        pc.getPeers().stream().forEach(
                it -> {
                    InterestedCommand ic = new InterestedCommand(super.targetHash);
                }
        );
    }
}
