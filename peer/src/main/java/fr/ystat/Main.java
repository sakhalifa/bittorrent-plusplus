package fr.ystat;

import fr.ystat.config.IConfigurationManager;
import fr.ystat.config.JsonConfigurationManager;
import fr.ystat.config.exceptions.ConfigException;
import fr.ystat.files.CompletedFile;
import fr.ystat.files.FileInventory;
import fr.ystat.gui.LoadingForm;
import fr.ystat.gui.MainForm;
import fr.ystat.peer.seeder.Seeder;
import fr.ystat.tracker.TrackerConnection;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

	private static JFrame frame;
	private static JPanel cards;

	@Getter
	private static IConfigurationManager configurationManager;
	@Getter
	private static TrackerConnection trackerConnection;
	@Getter
	private static Seeder seeder;

	public static void handleTrackerConnection() {
		seeder.serve();
		cards.add(new MainForm().getContentPane(), "MAIN_FORM");
		frame.pack();
		CardLayout cl = (CardLayout) cards.getLayout();
		SwingUtilities.invokeLater(() -> {
			cl.show(cards, "MAIN_FORM");
		});
	}

	private static void validateConfiguration(IConfigurationManager config) {
		// peer port validation
		if (config.peerPort() <= 0)
			throw new ConfigException("peerport must be greater than 0");
		if (config.peerPort() >= 65536) // unsigned short max val
			throw new ConfigException("peerport must be less than 65535");
		// tracker port validation
		if (config.trackerPort() <= 0)
			throw new ConfigException("trackerport must be greater than 0");
		if (config.trackerPort() >= 65536)
			throw new ConfigException("trackerport must be less than 65535");
		// tracker ip validation
		try {
			//noinspection ResultOfMethodCallIgnored
			InetAddress.getByName(config.trackerIP());
		} catch (UnknownHostException e) {
			throw new ConfigException("Invalid tracker IP");
		}
		// max leechers validation
		if (config.maxLeechers() <= 0)
			throw new ConfigException("maxLeechers must be greater than 0");
		// max seeders validation
		if (config.maxSeeders() <= 0)
			throw new ConfigException("maxSeeders must be greater than 0");
		// max message size validation
		if (config.maxMessageSize() <= 0)
			throw new ConfigException("maxMessageSize must be greater than 0");
		// default piece size validation
		if (config.defaultPieceSize() <= 0)
			throw new ConfigException("defaultPieceSize must be greater than 0");
		// update peers interval validation
		if (config.updatePeersIntervalMS() <= 0)
			throw new ConfigException("updatePeersIntervalMS must be greater than 0");
		// update tracker interval validation
		if (config.updateTrackerIntervalMS() <= 0)
			throw new ConfigException("updateTrackerIntervalMS must be greater than 0");
		// download path validation (and creation)
		File f = new File(config.downloadFolderPath());
		if (!f.exists() && !f.mkdirs())
			throw new ConfigException("could not create download folder");
	}

	private static void createAndShowGUI() {
		frame = new JFrame("Bittorrent plus plus ultra dingue");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.add(new LoadingForm().getContentPanel());
		cards.add(new LoadingForm().getContentPane(), "LOADING_FORM");
		frame.getContentPane().add(cards, BorderLayout.CENTER);
		//Display the window.
		frame.setLocationRelativeTo(null); // center on the screen
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) throws Exception {

		// Simple trick to set up leecher and seeder on 2 different port.
		if (args.length == 0) {
			configurationManager = new JsonConfigurationManager("config2.json");
		} else {
			configurationManager = new JsonConfigurationManager();
		}

		validateConfiguration(configurationManager);
		for (String arg : args) {
			FileInventory.getInstance().addStockedFile(
					CompletedFile.fromLocalFile(new File(arg), 2048L)
			);
		}
		seeder = new Seeder();
		cards = new JPanel(new CardLayout());
		SwingUtilities.invokeLater(Main::createAndShowGUI);
		trackerConnection = new TrackerConnection(InetAddress.getByName(configurationManager.trackerIP()),
				Main.configurationManager.trackerPort(),
				Main::handleTrackerConnection);

	}
}
