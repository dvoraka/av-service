package dvoraka.avservice.server.checker;

/**
 * Interface for various message checkers.
 */
public interface Checker extends Receiver, Sender {

    /**
     * Checks a message sending and receiving and returns the status.
     *
     * @return true if everything works
     */
    boolean check();
}
