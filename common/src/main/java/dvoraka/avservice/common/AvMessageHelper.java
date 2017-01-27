package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AvMessage;

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
        for (AvMessageListener listener : listeners) {
            listener.onAvMessage(message);
        }
    }

    /**
     * Prepares a response with an info.
     *
     * @param message   the origin message
     * @param virusInfo the info
     * @return new response
     */
    default AvMessage prepareResponse(AvMessage message, String virusInfo) {
        return message.createResponse(virusInfo);
    }

    /**
     * Prepares an error response with an error info.
     *
     * @param message      the origin message
     * @param errorMessage the error info
     * @return new response
     */
    default AvMessage prepareErrorResponse(AvMessage message, String errorMessage) {
        return message.createErrorResponse(errorMessage);
    }
}
