package dvoraka.avservice;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;

/**
 * Abstraction for message processing.
 */
public interface MessageProcessor {

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
     * Returns a processed message.
     *
     * @return a processed message
     */
    AvMessage getProcessedMessage();

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

    void removeProcessedAVMessageListener(ProcessedAvMessageListener listener);
}
