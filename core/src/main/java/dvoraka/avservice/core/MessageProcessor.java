package dvoraka.avservice.core;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.helper.AvMessageHelper;
import dvoraka.avservice.common.helper.ExecutorServiceHelper;

import java.util.function.Predicate;

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
     * Returns a message status with a given ID.
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

    /**
     * Filters out unwanted messages. The default (null) is pass everything to the processor.
     * If you specify a filter, only matched messages are eligible for the processing.
     *
     * @param filter the filter
     */
    void setInputFilter(Predicate<AvMessage> filter);
}
