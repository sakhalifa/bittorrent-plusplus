package fr.ystat.files;

import fr.ystat.files.exceptions.PartitionException;

import java.io.File;
import java.util.BitSet;

public class DownloadedFile extends StockedFile {

    private final BitSet bitSet;
    private final File[] partionnedFiles;

    public DownloadedFile(String name, long totalByteSize, long pieceByteSize, String hash) throws IllegalArgumentException {
        super(name, totalByteSize, pieceByteSize, hash);

        int pieceAmount = (int) ((this.totalSize + this.pieceSize - 1) / this.pieceSize);
        this.bitSet = new BitSet(pieceAmount);
        this.partionnedFiles = new File[pieceAmount];
    }

    public void addPartition(int partitionIndex, String data) throws PartitionException {
        if (bitSet.get(partitionIndex)) {
            throw new PartitionException(String.format("Partition %d already present.", partitionIndex));
        }
    }

    @Override
    public String getPartition(int partitionIndex) throws PartitionException {
        if (! bitSet.get(partitionIndex)) {
            throw new PartitionException(String.format("Partition %d not present.", partitionIndex));
        }
        // TODO
        return null;
    }
}
