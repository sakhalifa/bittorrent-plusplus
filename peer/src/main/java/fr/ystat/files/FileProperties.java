package fr.ystat.files;

import lombok.Value;
import org.tinylog.Logger;

@Value
public class FileProperties {
	String name;
	long size;
	long pieceSize;
	String hash;

	public FileProperties(String name, long size, long pieceSize, String hash) {
		this.hash = hash;
		if (pieceSize <= 0) {
			throw new IllegalArgumentException("File piece size must be greater than 1 byte.");
		}
		if (size <= 0) {
			throw new IllegalArgumentException("File total size must be greater than 1 byte.");
		}
		this.pieceSize = pieceSize;
		Logger.trace("New piece size of size {}", pieceSize);
		this.size = size;

		if (name.contains("/")) {
			throw new IllegalArgumentException("File name contains invalid characters! {/}");
		}

		this.name = name;
	}

	@Override
	public String toString(){
		return String.format("%s %d %d %s", name, size, pieceSize, hash);
	}
}
