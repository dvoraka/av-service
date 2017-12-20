package dvoraka.avservice.client.checker;

import dvoraka.avservice.client.send.AvMessageSender;

import java.util.function.BooleanSupplier;

/**
 * Interface for various message checkers.
 */
public interface Checker extends AvCheckReceiver, AvMessageSender, BooleanSupplier {

    /**
     * Checks a message sending and receiving and returns a status.
     *
     * @return true if everything works
     */
    boolean check();
}
