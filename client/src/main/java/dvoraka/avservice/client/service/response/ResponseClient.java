package dvoraka.avservice.client.service.response;

import dvoraka.avservice.common.data.AvMessage;

import java.util.concurrent.Future;

/**
 * Client interface for getting a response.
 */
public interface ResponseClient extends Future<AvMessage>, ResponseProvider<AvMessage> {

    /**
     * Returns a response message.
     *
     * @param id the request message ID
     * @return the response message or null if message is not available
     */
    AvMessage getResponse(String id);
}
