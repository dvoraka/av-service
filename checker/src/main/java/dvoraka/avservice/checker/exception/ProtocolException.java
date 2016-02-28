package dvoraka.avservice.checker.exception;

/**
 * Base class for protocol exceptions.
 *
 * @author dvoraka
 */
public class ProtocolException extends Exception {

    /**
     * Creates a new instance of <code>ProtocolException</code> without detail
     * message.
     */
    public ProtocolException() {
    }

    /**
     * Constructs an instance of <code>ProtocolException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ProtocolException(String msg) {
        super(msg);
    }
}
