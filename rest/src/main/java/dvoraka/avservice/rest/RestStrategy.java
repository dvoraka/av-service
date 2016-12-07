package dvoraka.avservice.rest;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;

/**
 * REST strategy interface.
 */
public interface RestStrategy {

    MessageStatus messageStatus(String id);

    MessageStatus messageStatus(String id, String serviceId);

    String messageServiceId(String id);

    /**
     * Checks an AV message.
     *
     * @param message the AV message
     */
    void messageCheck(AvMessage message);

    /**
     * Returns a response AV message.
     *
     * @param id the request message ID
     * @return the response AV message
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
