package fr.ystat.peer.leecher.exceptions;

public class FileAlreadyDownloadingException extends DownloadException{
    public FileAlreadyDownloadingException(String message) {
        super(message);
    }

    public FileAlreadyDownloadingException(){}

}
