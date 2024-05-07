package fr.ystat.peer.leecher.downloader;

import fr.ystat.files.DownloadedFile;
import fr.ystat.files.FileInventory;
import fr.ystat.files.FileProperties;
import fr.ystat.files.StockedFile;
import fr.ystat.peer.leecher.exceptions.DownloadException;
import fr.ystat.peer.leecher.exceptions.InvalidDownloaderException;

import java.lang.reflect.InvocationTargetException;

public abstract class FileDownloader {

    protected final DownloadedFile target;

    protected FileDownloader(DownloadedFile target) {
        this.target = target;
    }

    public static FileDownloader create(FileProperties properties, Class<? extends FileDownloader> downloaderClass) throws DownloadException {

        StockedFile localVersion = FileInventory.getInstance().getStockedFile(properties.getHash());
        if (localVersion == null){
            // Add it :D
            localVersion = new DownloadedFile(properties);
            FileInventory.getInstance().addStockedFile(localVersion);
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


    public abstract void startDownload() throws DownloadException;
}
