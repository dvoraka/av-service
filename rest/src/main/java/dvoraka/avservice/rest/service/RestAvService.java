package dvoraka.avservice.rest.service;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Rest AV service interface.
 */
public interface RestAvService {

    /**
     * Checks a file from the message.
     *
     * @param message the AV message
     */
    void checkMessage(AvMessage message);

    /**
     * Returns a response message.
     *
     * @param id the request message ID
     * @return the response message or null if message is not available
     */
    AvMessage getResponse(String id);
}
