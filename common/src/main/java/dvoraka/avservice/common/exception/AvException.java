package dvoraka.avservice.common.exception;

/**
 * Base exception class for AV service.
 */
public class AvException extends ServiceException {

    private static final long serialVersionUID = 1494553045042755302L;


    public AvException(String message) {
        super(message);
    }

    public AvException(String message, Throwable cause) {
        super(message, cause);
    }
}
