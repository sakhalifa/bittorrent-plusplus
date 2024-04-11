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
    private CompletedFile(FileProperties properties, File file) throws IllegalArgumentException {
        super(properties);
        this.file = file;
    }

    public static CompletedFile fromLocalFile(File localFile, long pieceByteSize) throws StockedFileException {
        String hashcode;
        try {
            hashcode = hashFile(localFile);
        } catch (IOException e) {
            throw new FileCreationException(e.getMessage());
        }

        return new CompletedFile(new FileProperties(localFile.getName(), localFile.getTotalSpace(), pieceByteSize, hashcode), localFile);
    }

    @SneakyThrows
    @Override
    public byte[] getPartition(int partitionIndex) throws PartitionException {
        try (RandomAccessFile accessFile = new RandomAccessFile(file, "r")) {
            byte[] buffer = new byte[(int) getProperties().getPieceSize()];
            long readBegin = partitionIndex * getProperties().getPieceSize();
            accessFile.read(buffer, (int) readBegin, (int) getProperties().getPieceSize());
            return buffer;
        }
    }

    @Override
    public String toString() {
        return String.format("%s %d %d %s", this.file.getName(), this.file.length(), this.getProperties().getPieceSize(), this.getProperties().getHash());
    }
}
