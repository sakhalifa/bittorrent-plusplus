package fr.ystat.tracker.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.ISendableCommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.CompletedFile;
import fr.ystat.files.DownloadedFile;
import fr.ystat.files.StockedFile;
import fr.ystat.server.Counter;
import fr.ystat.util.SerializationUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@CommandAnnotation(name = "announce")
public class AnnounceCommand implements ISendableCommand {

	private final Collection<StockedFile> seedingFiles;
	private final int port;

	public AnnounceCommand(int port, Collection<StockedFile> seedingFiles) {
		this.port = port;
		this.seedingFiles = seedingFiles;
	}

	@Override
	public String serialize() {
		Stream<DownloadedFile> partialFiles = SerializationUtils.filterByType(seedingFiles.stream(), DownloadedFile.class);
		Stream<CompletedFile> completeFiles = SerializationUtils.filterByType(seedingFiles.stream(), CompletedFile.class);
		return String.format(
				"announce listen %d seed %s leech %s",
				port,
				SerializationUtils.streamToString(completeFiles),
				SerializationUtils.streamToString(partialFiles));
	}
}
