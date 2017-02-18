package dvoraka.avservice.common.exception;

/**
 * Base exception class for the service.
 */
public class ServiceException extends Exception {

    private static final long serialVersionUID = 8081778416805215152L;


    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
