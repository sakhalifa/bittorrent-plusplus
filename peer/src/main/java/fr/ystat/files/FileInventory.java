package fr.ystat.files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class FileInventory {

    private static FileInventory INSTANCE = null;
    final private static Object mutex = new Object();

    final private ConcurrentMap<String, StockedFile> filesMap = new ConcurrentHashMap<>();
	private final Queue<Consumer<StockedFile>> fileAddedListeners = new ConcurrentLinkedQueue<>();
	private final Queue<Consumer<StockedFile>> fileRemovedListeners = new ConcurrentLinkedQueue<>();

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

	public boolean contains(String hash){
		return filesMap.containsKey(hash);
	}

    public void addStockedFile(StockedFile newFile) {
        filesMap.put(newFile.getProperties().getHash(), newFile);
		for(var listener : fileAddedListeners) {
			listener.accept(newFile);
		}
    }

	public boolean removeStockedFile(String hash) {
		StockedFile removed = filesMap.remove(hash);
		if(removed != null)
			for(var listener : fileRemovedListeners) {
				listener.accept(removed);
			}
		return removed != null;
	}

	public boolean removeStockedFile(StockedFile stockedFile) {
		return removeStockedFile(stockedFile.getProperties().getHash());
	}

    public StockedFile getStockedFile(String hash) {
        return filesMap.get(hash);
    }

	public Collection<StockedFile> getAllFiles() {
		return filesMap.values();
	}

	public void addOnFileAddedListener(Consumer<StockedFile> listener){
		fileAddedListeners.add(listener);
	}

	public void removeOnFileAddedListener(Consumer<StockedFile> listener){
		fileAddedListeners.remove(listener);
	}

	public void addOnFileRemovedListener(Consumer<StockedFile> listener){
		fileRemovedListeners.add(listener);
	}

	public void removeOnFileRemovedListener(Consumer<StockedFile> listener){
		fileRemovedListeners.remove(listener);
	}
}
