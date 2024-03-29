package fr.ystat.files;

import fr.ystat.files.exceptions.FileCreationException;
import fr.ystat.files.exceptions.PartitionException;
import fr.ystat.files.exceptions.StockedFileException;

import java.io.File;
import java.io.IOException;

public class CompleteFile extends StockedFile {

    private File file;

    public CompleteFile(String name, long totalByteSize, long pieceByteSize, String hash) throws IllegalArgumentException {
        super(name, totalByteSize, pieceByteSize, hash);
    }

    @Override
    public String getPartition(int partitionIndex) throws PartitionException {
        // TODO
        return null;
    }

    public static CompleteFile fromLocalFile(File localFile, long pieceByteSize) throws StockedFileException {

        String hashcode;
        try {
            hashcode = hashFile(localFile);
        } catch (IOException e) {
            throw new FileCreationException(e.getMessage());
        }

        CompleteFile stockedLocalFile = new CompleteFile(localFile.getName(), localFile.getTotalSpace(), pieceByteSize, hashcode);

        stockedLocalFile.file = localFile;

        return stockedLocalFile;
    }
}
