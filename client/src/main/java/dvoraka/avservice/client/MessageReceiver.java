package dvoraka.avservice.client;

import dvoraka.avservice.common.listener.MessageListener;

public interface MessageReceiver<T extends MessageListener> {

    /**
     * Adds a message listener.
     *
     * @param listener the listener
     */
    void addMessageListener(T listener);

    /**
     * Removes a message listener.
     *
     * @param listener the listener
     */
    void removeMessageListener(T listener);

    /**
     * Returns a listener count.
     *
     * @return the listener count
     */
    int getListenerCount();
}
