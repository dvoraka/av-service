package dvoraka.avservice;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;

/**
 * Abstraction for message processing.
 */
public interface MessageProcessor {

    /**
     * Sends a message to the processor.
     *
     * @param message the message
     */
    void sendMessage(AvMessage message);

    /**
     * Returns the message status with the given ID.
     *
     * @param id the message ID
     * @return the status
     */
    MessageStatus messageStatus(String id);

    /**
     * Starts processing.
     */
    void start();

    /**
     * Stops processing.
     */
    void stop();

    /**
     * Registers a new listener.
     *
     * @param listener the listener
     */
    void addProcessedAVMessageListener(AvMessageListener listener);

    /**
     * Removes a listener.
     *
     * @param listener the listener
     */
    void removeProcessedAVMessageListener(AvMessageListener listener);
}
