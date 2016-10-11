package dvoraka.avservice.common.exception;

/**
 * Exception for reaching last message in a queue.
 */
public class LastMessageException extends Exception {

    private static final long serialVersionUID = 7265332220841163851L;


    /**
     * Creates an instance of <code>LastMessageException</code> without detail
     * message.
     */
    public LastMessageException() {
        super();
    }
}
