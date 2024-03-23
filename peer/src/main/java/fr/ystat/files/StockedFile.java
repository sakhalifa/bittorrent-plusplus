package fr.ystat.files;

import java.util.BitSet;

import javax.naming.PartialResultException;

import fr.ystat.files.exceptions.FileCreationException;
import fr.ystat.files.exceptions.PartitionException;
import fr.ystat.files.exceptions.StockedFileException;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StockedFile {
    @Getter
    private final String hash;
    private final long pieceSize;  // Size in bytes
    private final long totalSize;  // Size in bytes
    private final BitSet bitSet;
    private File completeFile;
    private File[] partionnedFiles;

    public StockedFile(String name, long totalByteSize, long pieceByteSize, String hash) throws IllegalArgumentException {
        this.hash = hash;
        if (pieceByteSize <= 0){
            throw new IllegalArgumentException("File piece size must be greater than 1 byte.");
        }
        if (totalByteSize <= 0){
            throw new IllegalArgumentException("File total size must be greater than 1 byte.");
        }
        this.pieceSize = pieceByteSize;
        this.totalSize = totalByteSize;

        int pieceAmount = (int)((this.totalSize + this.pieceSize - 1) / this.pieceSize);

        this.bitSet = new BitSet(pieceAmount);
        this.partionnedFiles = new File[pieceAmount];
    }

    @SneakyThrows(NoSuchAlgorithmException.class)
    private static String hashFile(File file) throws IOException {
        byte[] fileData = Files.readAllBytes(file.toPath());
        byte[] hash = MessageDigest.getInstance("MD5").digest(fileData);
        String checksum = new BigInteger(1, hash).toString(16);
        return checksum;
    }

    public static StockedFile fromLocalFile(File localFile, long pieceByteSize) throws StockedFileException {

        String hashcode; 
        try {
            hashcode = hashFile(localFile);
        } catch (IOException e){
            throw new FileCreationException(e.getMessage());
        }
        
        StockedFile stockedLocalFile = StockedFile(localFile.getName(), localFile.getTotalSpace(), pieceByteSize, hashcode);

        stockedLocalFile.completeFile = localFile;

        return stockedLocalFile;
    }        

    public void addPartition(int partitionIndex, String data) throws PartitionException {
        if (bitSet.get(partitionIndex)){
            throw new PartitionException(String.format("Partition %d already present.", partitionIndex));
        }


    }

    // public String getPartition(int partitionIndex) throws PartitionException {
    //     // TODO
    // }


}
