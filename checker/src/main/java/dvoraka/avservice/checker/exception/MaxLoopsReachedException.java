package dvoraka.avservice.checker.exception;

/**
 * @author dvoraka
 */
public class MaxLoopsReachedException extends Exception {

    /**
     * Creates a new instance of <code>MaxLoopsReachedException</code> without
     * detail message.
     */
    public MaxLoopsReachedException() {
    }

    /**
     * Constructs an instance of <code>MaxLoopsReachedException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public MaxLoopsReachedException(String msg) {
        super(msg);
    }
}
