package dvoraka.avservice.server.service;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Anti-virus service client. It is used for client connections to the AV service.
 */
public interface AvServiceClient {

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
