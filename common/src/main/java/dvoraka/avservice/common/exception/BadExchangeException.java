package dvoraka.avservice.common.exception;

/**
 * Exception for messages with bad app-id (bad message routing).
 */
public class BadExchangeException extends ProtocolException {

    private static final long serialVersionUID = 8996931630469185853L;


    /**
     * Creates an instance of <code>BadExchangeException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BadExchangeException(String msg) {
        super(msg);
    }
}
