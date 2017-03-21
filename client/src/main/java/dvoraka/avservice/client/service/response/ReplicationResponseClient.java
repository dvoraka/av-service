package dvoraka.avservice.client.service.response;

/**
 * Client interface for getting a replication response.
 */
public interface ReplicationResponseClient {

    /**
     * Returns a replication response message.
     *
     * @param id the request message ID
     * @return the response message or null if message is not available
     */
    ReplicationMessageList getResponse(String id);

    ReplicationMessageList getResponseWait(String id, long waitTime);
}
