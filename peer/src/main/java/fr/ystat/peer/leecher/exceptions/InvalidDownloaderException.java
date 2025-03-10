package fr.ystat.peer.leecher.exceptions;

public class InvalidDownloaderException extends DownloadException {
    public InvalidDownloaderException(String message) {
        super(message);
    }

    public InvalidDownloaderException() {
        super();
    }
}
