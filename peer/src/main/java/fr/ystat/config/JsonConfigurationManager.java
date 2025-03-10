package fr.ystat.config;

import com.google.gson.Gson;
import org.tinylog.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class JsonConfigurationManager implements IConfigurationManager {
	private final JsonConfig config;

	private static final Gson gson = new Gson();

	private static void createFileFromResource(File f) {
		try (var inputStream = JsonConfigurationManager.class.getResourceAsStream("/" + f.getPath())) {
			if(inputStream == null) {
				System.err.println("Resource not found: " + f.getPath());
				System.exit(1);
			}
			Files.copy(inputStream, f.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			System.err.println("Error while loading config.json");
			System.exit(1);
		}
	}

	public JsonConfigurationManager(String configPath){
		File f = new File(configPath);
		if (!f.exists())
			createFileFromResource(f);
		try(var reader = new FileReader(f)) {
			this.config = gson.fromJson(reader, JsonConfig.class);
			Logger.trace("UPDATE TRACKER MS " + this.config.getUpdateTrackerIntervalMS());

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public JsonConfigurationManager() {
		this("config.json");
	}

	@Override
	public int peerPort() {
		return config.getPeerPort();
	}

	@Override
	public int trackerPort() {
		return config.getTrackerPort();
	}

	@Override
	public String trackerIP() {
		return config.getTrackerIP();
	}

	@Override
	public int maxLeechers() {
		return config.getMaxLeechers();
	}

	@Override
	public int maxSeeders() {
		return config.getMaxSeeders();
	}

	@Override
	public long maxMessageSize() {
		return config.getMaxMessageSize();
	}

	@Override
	public long defaultPieceSize() {
		return config.getDefaultPieceSize();
	}

	@Override
	public long updatePeersIntervalMS() {
		return config.getUpdatePeersIntervalMS();
	}

	@Override
	public long updateTrackerIntervalMS() {
		return config.getUpdateTrackerIntervalMS();
	}

	@Override
	public String downloadFolderPath() {
		return config.getDownloadFolderPath();
	}
}
