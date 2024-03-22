package fr.ystat.config;

public interface IConfigurationManager {
	int getPeerPort();
	int getTrackerPort();
	String getTrackerIP();
	int getMaxLeechers();
	int getMaxSeeders();
	long getMaxMessageSize();
	long defaultPieceSize();
	long updatePeersIntervalMS();
	long updateTrackerIntervalMS();
}
