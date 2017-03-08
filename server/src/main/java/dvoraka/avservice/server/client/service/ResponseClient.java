package dvoraka.avservice.server.client.service;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Client interface for getting a response.
 */
public interface ResponseClient {

    /**
     * Returns a response message.
     *
     * @param id the request message ID
     * @return the response message or null if message is not available
     */
    AvMessage getResponse(String id);
}
