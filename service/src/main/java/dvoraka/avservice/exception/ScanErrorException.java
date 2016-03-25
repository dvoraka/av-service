package dvoraka.avservice.exception;

/**
 * Exception for errors while scanning.
 */
public class ScanErrorException extends AVException {


    public ScanErrorException(String message) {
        super(message);
    }

    public ScanErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
