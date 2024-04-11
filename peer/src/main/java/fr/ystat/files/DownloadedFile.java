package fr.ystat.files;

import fr.ystat.config.GlobalConfiguration;
import fr.ystat.files.exceptions.PartitionException;

import java.io.*;
import java.nio.file.Files;
import java.util.BitSet;

public class DownloadedFile extends StockedFile {

    private static final File downloadFolder;
    static {
        downloadFolder = new File(GlobalConfiguration.get().downloadFolderPath());
        downloadFolder.mkdirs();
    }

    private static final String PARTITION_BASE_NAME = "partition";

    private final BitSet bitSet;
    private final File[] partitionedFiles;
    private final File parentFolder;


    public DownloadedFile(FileProperties properties) throws IllegalArgumentException {
        super(properties);

        int pieceAmount = (int) ((this.getProperties().getSize() + this.getProperties().getPieceSize() - 1) / this.getProperties().getPieceSize());
        this.bitSet = new BitSet(pieceAmount);
        this.partitionedFiles = new File[pieceAmount];
        parentFolder = new File(downloadFolder, getProperties().getName());
        parentFolder.mkdir();
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
    }

    /**
     * Write data inside given file.
     */
    private void writeFile(File file, byte[] data) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(data);
        }
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
}
