package dvoraka.avservice.common.exception;

/**
 * Base class for protocol exceptions.
 *
 * @author dvoraka
 */
public class ProtocolException extends AvException {

    private static final long serialVersionUID = 7356284192079431265L;


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
