package fr.ystat.config;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class JsonConfig {
	int peerPort;
	int trackerPort;
	String trackerIP;
	int maxLeechers;
	int maxSeeders;
	long maxMessageSize;
	long defaultPieceSize;
	long updatePeersIntervalMS;
	long updateTrackerIntervalMS;
	String downloadFolderPath;
}
