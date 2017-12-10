package dvoraka.avservice.common.listener;

import dvoraka.avservice.common.data.Message;

/**
 * Common listener interface for messages.
 *
 * @param <T> the type of the message
 * @see Message
 */
public interface MessageListener<T extends Message> {

    /**
     * Receives messages.
     *
     * @param message the message
     */
    void onMessage(T message);
}
