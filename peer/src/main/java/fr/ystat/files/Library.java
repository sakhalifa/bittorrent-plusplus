package fr.ystat.files;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Library {

    private static Library INSTANCE = null;
    final private static Object mutex = new Object();

    final private ConcurrentMap<String, StockedFile> filesMap = new ConcurrentHashMap<>(); ;

    private Library() {}

    public static Library getInstance() {
		Library result = INSTANCE;
		if (result == null) {
			synchronized (mutex) {
				result = INSTANCE;
				if (result == null) {
                    INSTANCE = result = new Library();
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
