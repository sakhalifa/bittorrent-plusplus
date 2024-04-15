package fr.ystat;

import fr.ystat.config.DummyConfigurationManager;
import fr.ystat.config.IConfigurationManager;
import fr.ystat.files.FileProperties;
import fr.ystat.gui.MainFrame;
import fr.ystat.tracker.TrackerConnection;
import fr.ystat.tracker.commands.client.ListCommand;
import fr.ystat.tracker.commands.client.PeersCommand;
import fr.ystat.tracker.commands.server.GetFileCommand;
import fr.ystat.tracker.commands.server.LookCommand;
import fr.ystat.tracker.criterions.FilenameCriterion;
import fr.ystat.tracker.criterions.FilesizeCriterion;
import lombok.Getter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Scanner;

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
		configurationManager = new DummyConfigurationManager(); // TODO: better config xd
		tc = new TrackerConnection(InetAddress.getByName("localhost"), 6666, Main::handleTrackerConnection);
		new MainFrame(); // TODO: Actually have the GUI. Right now it's just to hang
	}
}
