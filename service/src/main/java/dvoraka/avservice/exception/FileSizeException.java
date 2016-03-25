package dvoraka.avservice.exception;

/**
 * Exception for file size.
 */
public class FileSizeException extends AVException {


    public FileSizeException(String message) {
        super(message);
    }

    public FileSizeException(String message, Throwable cause) {
        super(message, cause);
    }
}
