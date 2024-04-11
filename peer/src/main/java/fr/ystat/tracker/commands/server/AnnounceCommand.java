package fr.ystat.tracker.commands.server;

import fr.ystat.commands.CommandAnnotation;
import fr.ystat.commands.ICommand;
import fr.ystat.commands.exceptions.CommandException;
import fr.ystat.files.CompletedFile;
import fr.ystat.files.DownloadedFile;
import fr.ystat.files.StockedFile;
import fr.ystat.server.Counter;
import fr.ystat.util.SerializationUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@CommandAnnotation(name = "announce") // TODO: is it necessary?
public class AnnounceCommand implements ICommand {

	private final Collection<StockedFile> seedingFiles;
	private final int port;

	public AnnounceCommand(int port, Collection<StockedFile> seedingFiles) {
		this.port = port;
		this.seedingFiles = seedingFiles;
	}

	@Override
	public String apply(Counter counter) throws CommandException {
		throw new UnsupportedOperationException("Cannot use 'apply' on announce tracker command.");
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
