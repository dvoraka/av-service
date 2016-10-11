package dvoraka.avservice.common.exception;

/**
 * Exception for a messages with unknown protocol error message.
 *
 * @author dvoraka
 */
public class UnknownProtocolException extends ProtocolException {

    private static final long serialVersionUID = 9060451516589672630L;


    /**
     * Creates an instance of <code>UnknownProtoclException</code> with the
     * specified detail message.
     *
     * @param msg the detail message
     */
    public UnknownProtocolException(String msg) {
        super(msg);
    }
}
