package fr.ystat;

import fr.ystat.config.IConfigurationManager;
import fr.ystat.config.JsonConfigurationManager;
import fr.ystat.config.exceptions.ConfigException;
import fr.ystat.files.FileProperties;
import fr.ystat.tracker.TrackerConnection;
import fr.ystat.tracker.commands.client.ListCommand;
import fr.ystat.tracker.commands.client.PeersCommand;
import fr.ystat.tracker.commands.server.GetFileCommand;
import fr.ystat.tracker.commands.server.LookCommand;
import fr.ystat.tracker.criterions.FilenameCriterion;
import fr.ystat.tracker.criterions.FilesizeCriterion;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Main {
	@Getter
	private static IConfigurationManager configurationManager;

	private static TrackerConnection tc;

	public static void handleTrackerConnection(){
		tc.sendLook(new LookCommand(List.of(new FilenameCriterion("file_a.dat"), new FilesizeCriterion(1048576L, FilesizeCriterion.ComparisonType.GT))), Main::handleTrackerList, () -> {
			System.err.println("Failure on list command >:(");
			System.exit(1);
		});
	}

	private static void handleTrackerList(ListCommand listCommand) {
		if(listCommand.getFileProperties().isEmpty())
			System.out.println("No result :(");
		FileProperties fp = listCommand.getFileProperties().get(0);
		tc.sendGetFile(new GetFileCommand(fp.getHash()), Main::handleTrackerPeers, () -> {
			System.err.println("Failure on getfile >:(");
			System.exit(1);
		});
	}

	private static void handleTrackerPeers(PeersCommand peersCommand) {
		System.out.println(peersCommand.getPeers());
		System.exit(0);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		configurationManager = new JsonConfigurationManager();
		validateConfiguration(configurationManager);
//		tc = new TrackerConnection(InetAddress.getByName("localhost"), 6666, Main::handleTrackerConnection);
//		new MainFrame(); // TODO: Actually have the GUI. Right now it's just to hang
	}

	private static void validateConfiguration(IConfigurationManager config) {
		// peer port validation
		if(config.peerPort() <= 0)
			throw new ConfigException("peerport must be greater than 0");
		if(config.peerPort() >= 65536) // unsigned short max val
			throw new ConfigException("peerport must be less than 65535");
		// tracker port validation
		if(config.trackerPort() <= 0)
			throw new ConfigException("trackerport must be greater than 0");
		if(config.trackerPort() >= 65536)
			throw new ConfigException("trackerport must be less than 65535");
		// tracker ip validation
		try{
			//noinspection ResultOfMethodCallIgnored
			InetAddress.getByName(config.trackerIP());
		} catch (UnknownHostException e) {
			throw new ConfigException("Invalid tracker IP");
		}
		// max leechers validation
		if(config.maxLeechers() <= 0)
			throw new ConfigException("maxLeechers must be greater than 0");
		// max seeders validation
		if(config.maxSeeders() <= 0)
			throw new ConfigException("maxSeeders must be greater than 0");
		// max message size validation
		if(config.maxMessageSize() <= 0)
			throw new ConfigException("maxMessageSize must be greater than 0");
		// default piece size validation
		if(config.defaultPieceSize() <= 0)
			throw new ConfigException("defaultPieceSize must be greater than 0");
		// update peers interval validation
		if(config.updatePeersIntervalMS() <= 0)
			throw new ConfigException("updatePeersIntervalMS must be greater than 0");
		// update tracker interval validation
		if(config.updateTrackerIntervalMS() <= 0)
			throw new ConfigException("updateTrackerIntervalMS must be greater than 0");
		// download path validation (and creation)
		File f = new File(config.downloadFolderPath());
		if(!f.exists() && !f.mkdirs())
			throw new ConfigException("could not create download folder");
	}
}
