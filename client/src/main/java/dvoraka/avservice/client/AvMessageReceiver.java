package dvoraka.avservice.client;

import dvoraka.avservice.common.listener.AvMessageListener;

/**
 * Interface for AV message receiving.
 */
public interface AvMessageReceiver {

    /**
     * Adds a listener.
     *
     * @param listener the listener
     */
    void addAvMessageListener(AvMessageListener listener);

    /**
     * Removes a listener.
     *
     * @param listener the listener
     */
    void removeAvMessageListener(AvMessageListener listener);

    /**
     * Returns a listener count.
     *
     * @return the listener count
     */
    int getListenerCount();
}
