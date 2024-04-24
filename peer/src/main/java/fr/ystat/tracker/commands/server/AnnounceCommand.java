package fr.ystat.tracker.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ISendableCommand;
import fr.ystat.files.CompletedFile;
import fr.ystat.files.DownloadedFile;
import fr.ystat.files.StockedFile;
import fr.ystat.util.SerializationUtils;

import java.util.Collection;
import java.util.stream.Stream;

@CommandAnnotation(name = "announce")
public class AnnounceCommand implements ISendableCommand {

	private final Collection<StockedFile> seedingFiles;
	private final int port;
	private final boolean isUpdate;

	public AnnounceCommand(int port, Collection<StockedFile> seedingFiles) {
		this(port, seedingFiles, false);
	}

	public AnnounceCommand(int port, Collection<StockedFile> seedingFiles, boolean isUpdate) {
		this.port = port;
		this.seedingFiles = seedingFiles;
		this.isUpdate = isUpdate;
	}

	@Override
	public String serialize() {
		Stream<DownloadedFile> partialFiles = SerializationUtils.filterByType(seedingFiles.stream(), DownloadedFile.class);
		Stream<CompletedFile> completeFiles = SerializationUtils.filterByType(seedingFiles.stream(), CompletedFile.class);
		return String.format(
				"%s listen %d seed %s leech %s",
				isUpdate ? "update" : "announce",
				port,
				SerializationUtils.streamToString(completeFiles),
				SerializationUtils.streamToString(partialFiles));
	}
}
