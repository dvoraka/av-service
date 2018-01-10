package dvoraka.avservice.common.helper;

import dvoraka.avservice.common.data.Message;
import dvoraka.avservice.common.listener.MessageListener;

/**
 * Helper interface for messages.
 */
public interface MessageHelper {

    /**
     * Notifies a message to all listeners.
     *
     * @param listeners the listeners
     * @param message   the message
     * @param <M>       the message type
     * @param <L>       the listener type
     */
    default <M extends Message, L extends MessageListener<M>>
    void notifyListeners(Iterable<L> listeners, M message) {
        listeners.forEach(listener -> listener.onMessage(message));
    }
}
