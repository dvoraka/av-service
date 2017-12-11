package dvoraka.avservice.client;

import dvoraka.avservice.common.data.Message;
import dvoraka.avservice.common.listener.MessageListener;

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
