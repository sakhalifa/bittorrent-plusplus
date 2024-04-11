package fr.ystat.files;

import fr.ystat.files.exceptions.FileCreationException;
import fr.ystat.files.exceptions.PartitionException;
import fr.ystat.files.exceptions.StockedFileException;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class CompletedFile extends StockedFile {

    private final File file;

    /**
     * Use fromLocalFile to instantiate a new CompleteFile
     */
    private CompletedFile(String name, long totalByteSize, long pieceByteSize, String hash, File file) throws IllegalArgumentException {
        super(name, totalByteSize, pieceByteSize, hash);
        this.file = file;
    }

    public static CompletedFile fromLocalFile(File localFile, long pieceByteSize) throws StockedFileException {
        String hashcode;
        try {
            hashcode = hashFile(localFile);
        } catch (IOException e) {
            throw new FileCreationException(e.getMessage());
        }

        return new CompletedFile(localFile.getName(), localFile.getTotalSpace(), pieceByteSize, hashcode, localFile);
    }

    @SneakyThrows
    @Override
    public byte[] getPartition(int partitionIndex) throws PartitionException {
        try (RandomAccessFile accessFile = new RandomAccessFile(file, "r")) {
            byte[] buffer = new byte[(int) pieceSize];
            long readBegin = partitionIndex * pieceSize;
            accessFile.read(buffer, (int) readBegin, (int) pieceSize);
            return buffer;
        }
    }

    @Override
    public String toString() {
        return String.format("%s %d %d %s", this.file.getName(), this.file.length(), this.pieceSize, this.hash);
    }
}
