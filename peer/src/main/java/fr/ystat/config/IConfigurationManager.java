package fr.ystat.config;

public interface IConfigurationManager {
	int peerPort();
	int trackerPort();
	String trackerIP();
	int maxLeechers();
	int maxSeeders();
	long maxMessageSize();
	long defaultPieceSize();
	long updatePeersIntervalMS();
	long updateTrackerIntervalMS();
	String downloadFolderPath();
}
