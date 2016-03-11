package dvoraka.avservice;

import dvoraka.avservice.data.AVMessage;

/**
 * Abstraction for message processing.
 */
public interface MessageProcessor {

    void sendMessage(AVMessage message);

    /**
     * Returns the message status with the given ID.
     *
     * @param id the message ID
     * @return the status
     */
    MessageStatus messageStatus(String id);

    /**
     * Returns true if any message is prepared.
     *
     * @return true if any message is prepared
     */
    boolean hasProcessedMessage();

    /**
     * Returns a processed message.
     *
     * @return a processed message
     */
    AVMessage getProcessedMessage();

    /**
     * Stops processing.
     */
    void stop();

    /**
     * Registers a new listener.
     *
     * @param listener the listener
     */
    void addAVMessageListener(AVMessageListener listener);
}
