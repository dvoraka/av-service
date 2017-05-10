package dvoraka.avservice.core;

import dvoraka.avservice.common.AvMessageHelper;
import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.service.ExecutorServiceHelper;

/**
 * Abstraction for message processing.
 */
public interface MessageProcessor extends AvMessageHelper, ExecutorServiceHelper {

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
