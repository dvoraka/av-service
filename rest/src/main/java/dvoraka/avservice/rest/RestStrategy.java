package dvoraka.avservice.rest;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;

/**
 * REST strategy interface.
 */
public interface RestStrategy {

    /**
     * Returns a message status.
     *
     * @param id the message ID
     * @return the status
     */
    MessageStatus messageStatus(String id);

    /**
     * Returns a message status.
     *
     * @param id        the message ID
     * @param serviceId the service ID
     * @return the status
     */
    MessageStatus messageStatus(String id, String serviceId);

    /**
     * Returns a service ID for the given message.
     *
     * @param id the message ID
     * @return the service ID
     */
    String messageServiceId(String id);

    /**
     * Checks an AV message.
     *
     * @param message the AV message
     */
    void checkMessage(AvMessage message);

    /**
     * Saves an AV message.
     *
     * @param message the message.
     */
    void saveMessage(AvMessage message);

    /**
     * Returns a response AV message.
     *
     * @param id the request message ID
     * @return the response AV message or null if message is not available
     */
    AvMessage getResponse(String id);

    /**
     * Starts the service.
     */
    void start();

    /**
     * Stops the service.
     */
    void stop();
}
