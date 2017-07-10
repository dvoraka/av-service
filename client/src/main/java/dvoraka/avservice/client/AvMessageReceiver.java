package dvoraka.avservice.client;

import dvoraka.avservice.common.AvMessageListener;

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
}
