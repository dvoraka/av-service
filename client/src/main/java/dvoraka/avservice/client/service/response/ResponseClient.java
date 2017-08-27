package dvoraka.avservice.client.service.response;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Client interface for getting a response.
 */
public interface ResponseClient extends ResponseProvider<AvMessage> {

    /**
     * Returns a response message.
     *
     * @param id the request message ID
     * @return the response message or null if message is not available
     */
    @Override
    AvMessage getResponse(String id);
}
