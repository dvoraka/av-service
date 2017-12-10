package dvoraka.avservice.common.helper;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.listener.AvMessageListener;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Helper interface for AV messages.
 */
public interface AvMessageHelper {

    /**
     * Notifies a message to all listeners.
     *
     * @param listeners the listeners
     * @param message   the message
     */
    default void notifyListeners(Iterable<AvMessageListener> listeners, AvMessage message) {
        listeners.forEach(listener -> listener.onMessage(message));
    }

    /**
     * Prepares a check response with an info.
     *
     * @param message   the origin message
     * @param virusInfo the info
     * @return new response
     */
    default AvMessage prepareResponse(AvMessage message, String virusInfo) {
        return message.createCheckResponse(virusInfo);
    }

    /**
     * Prepares an error check response with an error info.
     *
     * @param message      the origin message
     * @param errorMessage the error info
     * @return new response
     */
    default AvMessage prepareErrorResponse(AvMessage message, String errorMessage) {
        return message.createErrorResponse(errorMessage);
    }

    /**
     * Checks a condition.
     *
     * @param predicate the predicate
     * @param item      the item to check
     * @param <T>       the type of the item
     * @return the result
     */
    default <T> boolean checkCondition(Predicate<T> predicate, T item) {
        return predicate.test(item);
    }

    /**
     * Checks if all conditions in a stream are true.
     *
     * @param conditions   the stream of conditions
     * @param originalData the original data
     * @param actualData   the actual data
     * @param <T>          the type of condition items
     * @return the result
     */
    default <T> boolean checkConditions(
            Stream<? extends BiPredicate<T, T>> conditions,
            T originalData,
            T actualData
    ) {
        return conditions.allMatch(condition -> condition.test(originalData, actualData));
    }
}
