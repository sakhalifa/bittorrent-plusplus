package fr.ystat.config;

public class DummyConfigurationManager implements IConfigurationManager{
	@Override
	public int getPeerPort() {
		return 5697;
	}

	@Override
	public int getTrackerPort() {
		return 6666;
	}

	@Override
	public String getTrackerIP() {
		return "127.0.0.1";
	}

	@Override
	public int getMaxLeechers() {
		return 20;
	}

	@Override
	public int getMaxSeeders() {
		return 5;
	}

	@Override
	public long getMaxMessageSize() {
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
}
