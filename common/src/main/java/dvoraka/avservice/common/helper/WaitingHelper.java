package dvoraka.avservice.common.helper;

import java.util.function.BooleanSupplier;

/**
 * Waiting helper.
 */
public interface WaitingHelper {

    /**
     * Blocks until a condition is met.
     *
     * @param supplier the boolean supplier
     */
    default void waitUntil(BooleanSupplier supplier) {
        final int checkDelayMs = 10;
        while (!supplier.getAsBoolean()) {
            try {
                Thread.sleep(checkDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                return;
            }
        }
    }
}
