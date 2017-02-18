package dvoraka.avservice.common.exception;

/**
 * Exception for errors while scanning.
 */
public class ScanException extends AvException {

    private static final long serialVersionUID = 4069343654352794190L;


    public ScanException(String message) {
        super(message);
    }

    public ScanException(String message, Throwable cause) {
        super(message, cause);
    }
}
