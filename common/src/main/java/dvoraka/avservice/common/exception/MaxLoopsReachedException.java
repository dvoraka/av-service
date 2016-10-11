package dvoraka.avservice.common.exception;

/**
 * Exception for reaching max loops count.
 */
public class MaxLoopsReachedException extends Exception {

    private static final long serialVersionUID = 4905744016855020133L;


    /**
     * Creates an instance of <code>MaxLoopsReachedException</code> without
     * detail message.
     */
    public MaxLoopsReachedException() {
        super();
    }
}
