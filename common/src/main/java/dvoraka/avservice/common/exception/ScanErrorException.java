package dvoraka.avservice.common.exception;

/**
 * Exception for errors while scanning.
 */
public class ScanErrorException extends AvException {

    private static final long serialVersionUID = 4069343654352794190L;


    public ScanErrorException(String message) {
        super(message);
    }

    public ScanErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
