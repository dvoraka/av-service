package dvoraka.avservice.checker.exception;

/**
 * Exception for messages with bad app-id.
 */
public class BadExchangeException extends ProtocolException {

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
