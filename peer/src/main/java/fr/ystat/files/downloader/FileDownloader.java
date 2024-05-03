package fr.ystat.files.downloader;

public abstract class FileDownloader {

    protected final String targetHash;

    public FileDownloader(String targetHash) {
        this.targetHash = targetHash;
    }

    abstract void startDownload();
}
