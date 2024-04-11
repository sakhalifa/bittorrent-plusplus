package fr.ystat.files;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FileInventory {

    private static FileInventory INSTANCE = null;
    final private static Object mutex = new Object();

    final private ConcurrentMap<String, StockedFile> filesMap = new ConcurrentHashMap<>();

    private FileInventory() {}

    public static FileInventory getInstance() {
		FileInventory result = INSTANCE;
		if (result == null) {
			synchronized (mutex) {
				result = INSTANCE;
				if (result == null) {
                    INSTANCE = result = new FileInventory();
                }
			}
		}
		return result;
	}

    public void addStockedFile(StockedFile newFile) {
        filesMap.put(newFile.getHash(), newFile);
    }

    public StockedFile getStockedFile(String hash) {
        return filesMap.get(hash);
    }
}
