package fr.ystat.peer.leecher.exceptions;

public class DownloadException extends Exception{
    public DownloadException(String message) {
        super(message);
    }

    public DownloadException() {}
}
