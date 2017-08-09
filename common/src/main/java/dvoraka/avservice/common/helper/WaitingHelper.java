package dvoraka.avservice.common.helper;

import java.util.function.BooleanSupplier;

/**
 * Waiting helper.
 */
public interface WaitingHelper {

    /**
     * Blocks until condition is met.
     *
     * @param until the condition
     */
    default void waitUntil(BooleanSupplier until) {
        final int checkDelay = 10;
        while (!until.getAsBoolean()) {
            try {
                Thread.sleep(checkDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                return;
            }
        }
    }
}
