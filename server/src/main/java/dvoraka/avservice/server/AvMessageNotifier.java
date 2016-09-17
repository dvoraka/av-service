package dvoraka.avservice.server;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;

import java.util.Collection;

/**
 * AV message notifier.
 */
public interface AvMessageNotifier {

    /**
     * Notifies a message to all listeners.
     *
     * @param listeners the collection of listeners
     * @param message the message
     */
    default void notifyListeners(Collection<AvMessageListener> listeners, AvMessage message) {
        for (AvMessageListener listener : listeners) {
            listener.onAvMessage(message);
        }
    }
}
