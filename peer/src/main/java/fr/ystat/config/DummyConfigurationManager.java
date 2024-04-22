package fr.ystat.config;

public class  DummyConfigurationManager implements IConfigurationManager{
	@Override
	public int peerPort() {
		return 5697;
	}

	@Override
	public int trackerPort() {
		return 6666;
	}

	@Override
	public String trackerIP() {
		return "127.0.0.1";
	}

	@Override
	public int maxLeechers() {
		return 20;
	}

	@Override
	public int maxSeeders() {
		return 5;
	}

	@Override
	public long maxMessageSize() {
		return 4 * 1024 * 1024;
	}

	@Override
	public long defaultPieceSize() {
		return 2048;
	}

	@Override
	public long updatePeersIntervalMS() {
		return 1000 * 30;
	}

	@Override
	public long updateTrackerIntervalMS() {
		return 1000 * 30;
	}

	@Override
	public String downloadFolderPath() {
		return "downloads/";
	}
}
