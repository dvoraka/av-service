package dvoraka.avservice.common.exception;

/**
 * Base exception class for AV service.
 */
public class AvException extends Exception {


    public AvException(String message) {
        super(message);
    }

    public AvException(String message, Throwable cause) {
        super(message, cause);
    }
}
