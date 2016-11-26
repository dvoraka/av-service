package dvoraka.avservice;

import dvoraka.avservice.common.ProcessedAvMessageListener;
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
     * Returns true if any message is prepared.
     *
     * @return true if any message is prepared
     */
    boolean hasProcessedMessage();

    /**
     * Returns one processed message.
     *
     * @return the processed message
     */
    AvMessage getProcessedMessage();

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
    void addProcessedAVMessageListener(ProcessedAvMessageListener listener);

    /**
     * Removes a listener.
     *
     * @param listener the listener
     */
    void removeProcessedAVMessageListener(ProcessedAvMessageListener listener);
}
