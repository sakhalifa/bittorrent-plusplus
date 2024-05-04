package fr.ystat.peer.leecher.downloader;

import fr.ystat.files.DownloadedFile;
import fr.ystat.files.FileInventory;
import fr.ystat.files.StockedFile;
import fr.ystat.peer.leecher.exceptions.DownloadException;
import fr.ystat.peer.leecher.exceptions.InvalidDownloaderException;

import java.lang.reflect.InvocationTargetException;

public abstract class FileDownloader {

    protected final DownloadedFile target;

    protected FileDownloader(DownloadedFile target) {
        this.target = target;
    }

    public static FileDownloader create(String targetHash, Class<? extends FileDownloader> downloaderClass) throws DownloadException {
        StockedFile localVersion = FileInventory.getInstance().getStockedFile(targetHash);
        if (null == localVersion){
            // Should not happen as our GUI does not allow it, however, for more complete implementation, you may
            // want to do the following steps
            // Retrieve the file info from the tracker
            // Send a look command.

            throw new DownloadException("Downloading without local file information is not yet allowed");
        }

        if (!(localVersion instanceof DownloadedFile file_to_download)){
            throw new DownloadException("Impossible to download a file that is not tagged as a downloadable file");
        }
        try {
            return downloaderClass.getConstructor(DownloadedFile.class).newInstance(file_to_download);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new InvalidDownloaderException(String.format("Downloader type %s is invalid!", downloaderClass.getName()));
        }
    }


    abstract void startDownload() throws DownloadException;
}
