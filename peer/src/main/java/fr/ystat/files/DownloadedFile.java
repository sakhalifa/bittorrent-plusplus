package fr.ystat.files;

import fr.ystat.Main;
import fr.ystat.files.exceptions.PartitionException;

import java.io.*;
import java.nio.file.Files;
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
        if (! created) throw new PartitionException("Could not create new partition");
        // Write the data inside
        writeFile(addedPartition, data);

        partitionedFiles[partitionIndex] = addedPartition;
        bitSet.set(partitionIndex);
        for(var listener : onPartitionAddedListeners) {
            listener.accept(this, partitionIndex);
        }
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
     * @return a **copy** of the partition bitset
     */
    public AtomicBitSet getBitSet(){
        return new AtomicBitSet(bitSet);
    }

    @Override
    public byte[] getPartition(int partitionIndex) throws PartitionException, IOException {
        if (! bitSet.get(partitionIndex)) {
            throw new PartitionException(String.format("Partition %d not present.", partitionIndex));
        }
        File partition = partitionedFiles[partitionIndex];
        return Files.readAllBytes(partition.toPath());
    }

    @Override
    public String toString() {
        return this.getProperties().getHash();
    }

    public void addOnPartitionAddedListener(BiConsumer<StockedFile, Integer> listener){
        onPartitionAddedListeners.add(listener);
    }

    public void removeOnPartitionAddedListener(BiConsumer<StockedFile, Integer> listener){
        onPartitionAddedListeners.remove(listener);
    }
}
