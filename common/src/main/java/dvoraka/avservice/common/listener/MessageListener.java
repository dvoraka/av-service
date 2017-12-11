package dvoraka.avservice.common.listener;

import dvoraka.avservice.common.data.Message;

/**
 * Common listener interface for messages.
 *
 * @param <M> the type of the message
 * @see Message
 */
public interface MessageListener<M extends Message> {

    /**
     * Receives messages.
     *
     * @param message the message
     */
    void onMessage(M message);
}
