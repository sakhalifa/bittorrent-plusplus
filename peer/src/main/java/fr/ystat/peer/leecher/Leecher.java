package fr.ystat.peer.leecher;

import fr.ystat.Main;
import fr.ystat.files.DownloadedFile;
import fr.ystat.files.FileInventory;
import fr.ystat.files.FileProperties;
import fr.ystat.files.StockedFile;
import fr.ystat.peer.leecher.exceptions.DownloadException;
import fr.ystat.peer.leecher.exceptions.FileAlreadyDownloadingException;
import fr.ystat.tracker.commands.server.GetFileCommand;
import org.tinylog.Logger;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Leecher {
	private final Queue<SeederConnection> seederConnections;

	public Leecher() {
		this.seederConnections = new ConcurrentLinkedQueue<>();
	}

	public void startDownloadingFile(FileProperties properties) throws DownloadException {
		if (FileInventory.getInstance().contains(properties.getHash()))
			throw new FileAlreadyDownloadingException();
		StockedFile file = new DownloadedFile(properties);
		FileInventory.getInstance().addStockedFile(file);
		this.searchForSeeders(file);
	}

	private void searchForSeeders(StockedFile file) {
		// TODO an algorithm to try multiple seeders while keeping max value...
		Main.getTrackerConnection().sendGetFile(new GetFileCommand(file.getProperties().getHash()),
				peersCommand -> {
					var peers = peersCommand.getPeers();
					for(var peer : peers){
//						var seederConnection = new SeederConnection(peer);

					}
				},
				throwable -> {
					Logger.error("searchForSeeders: ", throwable);
				});
	}
}
