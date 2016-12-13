package dvoraka.avservice.server.checker;

import dvoraka.avservice.common.AvSender;

/**
 * Interface for various message checkers.
 */
public interface Checker extends Receiver, AvSender {

    /**
     * Checks a message sending and receiving and returns the status.
     *
     * @return true if everything works
     */
    boolean check();
}
