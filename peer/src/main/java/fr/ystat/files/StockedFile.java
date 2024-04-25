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

@Getter
public abstract class StockedFile {
	private final FileProperties properties;

	public StockedFile(FileProperties properties) throws IllegalArgumentException {
		this.properties = properties;
	}

	@SneakyThrows(NoSuchAlgorithmException.class)
	static String hashFile(File file) throws IOException {
		byte[] fileData = Files.readAllBytes(file.toPath());
		byte[] hash = MessageDigest.getInstance("MD5").digest(fileData);
		return new BigInteger(1, hash).toString(16);  // Checksum
	}

	abstract public byte[] getPartition(int partitionIndex) throws PartitionException, IOException;

	abstract public AtomicBitSet getBitSet();

}
