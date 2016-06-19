package dvoraka.avservice.checker.exception;

/**
 * Exception for reaching last message in a queue.
 */
public class LastMessageException extends Exception {

    /**
     * Creates a new instance of <code>LastMessageException</code> without detail
     * message.
     */
    public LastMessageException() {
        super();
    }
}
