package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AvMessage;

/**
 * AV message notifier.
 */
public interface AvMessageNotifier {

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
}
