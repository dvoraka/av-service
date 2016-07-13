package dvoraka.avservice.common.exception;

/**
 * Exception for errors while scanning.
 */
public class ScanErrorException extends AvException {


    public ScanErrorException(String message) {
        super(message);
    }

    public ScanErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
