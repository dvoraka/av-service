package dvoraka.avservice.exception;

/**
 * Base exception class for AV service.
 */
public class AVException extends Exception {


    public AVException(String message) {
        super(message);
    }

    public AVException(String message, Throwable cause) {
        super(message, cause);
    }
}
