package dvoraka.avservice.checker.exception;

/**
 * Exception for reaching last message in queue.
 *
 * @author dvoraka
 */
public class LastMessageException extends Exception {

    /**
     * Creates a new instance of <code>LastMessageException</code> without detail
     * message.
     */
    public LastMessageException() {
    }

    /**
     * Constructs an instance of <code>LastMessageException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public LastMessageException(String msg) {
        super(msg);
    }
}
