package dvoraka.avservice.checker.exception;

/**
 * Exception class for messages with bad app-id.
 *
 * @author dvoraka
 */
public class BadExchangeException extends ProtocolException {

    /**
     * Creates a new instance of <code>BadExchangeException</code> without
     * detail message.
     */
    public BadExchangeException() {
    }

    /**
     * Constructs an instance of <code>BadExchangeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BadExchangeException(String msg) {
        super(msg);
    }
}
