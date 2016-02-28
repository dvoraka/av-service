package dvoraka.avservice.checker.exception;

import dvoraka.avservice.checker.exception.ProtocolException;

/**
 * Exception class for messages with unknown error message.
 *
 * @author dvoraka
 */
public class UnknownProtocolException extends ProtocolException {

    /**
     * Creates a new instance of <code>UnknownProtoclException</code> without
     * detail message.
     */
    public UnknownProtocolException() {
    }

    /**
     * Constructs an instance of <code>UnknownProtoclException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UnknownProtocolException(String msg) {
        super(msg);
    }
}
