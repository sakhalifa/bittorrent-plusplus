package fr.ystat.files;

import fr.ystat.files.exceptions.PartitionException;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class StockedFile {
    @Getter
    protected final String hash;
    protected final long pieceSize;  // Size in bytes
    protected final long totalSize;  // Size in bytes
    protected final String name;

    public StockedFile(String name, long totalByteSize, long pieceByteSize, String hash) throws IllegalArgumentException {
        this.hash = hash;
        if (pieceByteSize <= 0) {
            throw new IllegalArgumentException("File piece size must be greater than 1 byte.");
        }
        if (totalByteSize <= 0) {
            throw new IllegalArgumentException("File total size must be greater than 1 byte.");
        }
        this.pieceSize = pieceByteSize;
        this.totalSize = totalByteSize;

        if (name.contains("/")) {
            throw new IllegalArgumentException("File name contains invalid characters! {/}");
        }

        this.name = name;
    }

    @SneakyThrows(NoSuchAlgorithmException.class)
    static String hashFile(File file) throws IOException {
        byte[] fileData = Files.readAllBytes(file.toPath());
        byte[] hash = MessageDigest.getInstance("MD5").digest(fileData);
        return new BigInteger(1, hash).toString(16);  // Checksum
    }

    abstract public byte[] getPartition(int partitionIndex) throws PartitionException, IOException;


}
