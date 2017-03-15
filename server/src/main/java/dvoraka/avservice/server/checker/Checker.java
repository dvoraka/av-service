package dvoraka.avservice.server.checker;

import dvoraka.avservice.client.AvMessageSender;

/**
 * Interface for various message checkers.
 */
public interface Checker extends Receiver, AvMessageSender {

    /**
     * Checks a message sending and receiving and returns the status.
     *
     * @return true if everything works
     */
    boolean check();
}
