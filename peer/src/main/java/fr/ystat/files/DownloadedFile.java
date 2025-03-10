package fr.ystat.files;

import fr.ystat.Main;
import fr.ystat.files.exceptions.PartitionException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.tinylog.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DownloadedFile extends StockedFile {

	private static final File downloadFolder;

	static {
		downloadFolder = new File(Main.getConfigurationManager().downloadFolderPath());
	}

	private static final String PARTITION_BASE_NAME = "partition";

	private final AtomicBitSet bitSet;
	private final File[] partitionedFiles;
	@Getter
	private final File parentFolder;
	private final Queue<BiConsumer<StockedFile, Integer>> onPartitionAddedListeners;


	public DownloadedFile(FileProperties properties) throws IllegalArgumentException {
		super(properties);

		this.onPartitionAddedListeners = new ConcurrentLinkedQueue<>();
		this.bitSet = new AtomicBitSet(properties);
		this.partitionedFiles = new File[bitSet.getLength()];
		parentFolder = new File(downloadFolder, getProperties().getName());
		parentFolder.mkdir();
	}

	public DownloadedFile(String name, long size, long pieceSize, String hash) {
		this(new FileProperties(name, size, pieceSize, hash));
	}

	public void addPartition(int partitionIndex, byte[] data, Consumer<Throwable> onFailure) {
		try {
			addPartition(partitionIndex, data);
		} catch (PartitionException | IOException e) {
			onFailure.accept(e);
		}
	}

	public void addPartition(int partitionIndex, byte[] data) throws PartitionException, IOException {
		if (bitSet.get(partitionIndex)) {
			throw new PartitionException(String.format("Partition %d already present.", partitionIndex));
		}


		// Create the partition
		File addedPartition = new File(parentFolder, String.format("%s.%d", PARTITION_BASE_NAME, partitionIndex));
		boolean created = addedPartition.createNewFile();
		// Ensure that it went well
		if (!created) throw new PartitionException("Could not create new partition");
		// Write the data inside
		Logger.trace("Writing into file {} : {}", addedPartition.getName(), data);

		// for the Last partition, we don't want to write everything
		if (partitionIndex == partitionedFiles.length - 1) {
			int length = (int) (this.getProperties().getSize() % this.getProperties().getPieceSize());
			Logger.trace("Last partition size {}", length);
			data = Arrays.copyOfRange(data, 0, length);

		}
		writeFile(addedPartition, data);

		partitionedFiles[partitionIndex] = addedPartition;
		bitSet.set(partitionIndex);
		for (var listener : onPartitionAddedListeners) {
			listener.accept(this, partitionIndex);
		}

		handleCompleteness();

	}

	@SneakyThrows
	private void handleCompleteness() {
		if (!bitSet.isFilled()) return;

		Logger.trace("Writing full file {}_final", parentFolder.getName());

		// Create final file
		File finalFile = new File(downloadFolder, parentFolder.getName() + "_final");
		try (var outFile = new RandomAccessFile(finalFile, "rw");
			 var outChannel = outFile.getChannel()) {
			for (File partitionedFile : partitionedFiles) {
				try (var inFile = new RandomAccessFile(partitionedFile, "r");
					 var inChannel = inFile.getChannel()) {
					inChannel.transferTo(0, inChannel.size(), outChannel);
				}
			}
		}
		Logger.trace("Full file {}_final wrote", parentFolder.getName());
	}

	/**
	 * Write data inside given file.
	 */
	private void writeFile(File file, byte[] data) throws IOException {
		try (FileOutputStream stream = new FileOutputStream(file)) {
			stream.write(data);
		}
	}

	/**
	 * Get a **copy** of the partition bitset.
	 *
	 * @return a **copy** of the partition bitset
	 */
	public AtomicBitSet getBitSet() {
		return new AtomicBitSet(bitSet);
	}

	@Override
	public byte[] getPartition(int partitionIndex) throws PartitionException, IOException {
		if (!bitSet.get(partitionIndex)) {
			throw new PartitionException(String.format("Partition %d not present.", partitionIndex));
		}
		File partition = partitionedFiles[partitionIndex];
		return Files.readAllBytes(partition.toPath());
	}

	@Override
	public String toString() {
		return this.getProperties().getHash();
	}

	public void addOnPartitionAddedListener(BiConsumer<StockedFile, Integer> listener) {
		onPartitionAddedListeners.add(listener);
	}

	public void removeOnPartitionAddedListener(BiConsumer<StockedFile, Integer> listener) {
		onPartitionAddedListeners.remove(listener);
	}
}
