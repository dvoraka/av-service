package dvoraka.avservice.client.receive;

import dvoraka.avservice.common.data.Message;
import dvoraka.avservice.common.listener.MessageListener;

/**
 * Interface for generic message receiving.
 *
 * @param <M> the message type
 * @param <L> the listener type
 */
public interface MessageReceiver<M extends Message, L extends MessageListener<M>> {

    /**
     * Adds a message listener.
     *
     * @param listener the listener
     */
    void addMessageListener(L listener);

    /**
     * Removes a message listener.
     *
     * @param listener the listener
     */
    void removeMessageListener(L listener);

    /**
     * Returns a listener count.
     *
     * @return the listener count
     */
    int getListenerCount();
}
