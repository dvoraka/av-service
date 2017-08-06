package dvoraka.avservice.client.service;

import dvoraka.avservice.common.data.replication.ReplicationMessage;

/**
 * Replication service client. It is used for client connections to the replication service.
 */
@FunctionalInterface
public interface ReplicationServiceClient {

    /**
     * Sends a replication message.
     *
     * @param message the message
     * @see dvoraka.avservice.common.replication.ReplicationHelper
     * @see dvoraka.avservice.common.replication.ReplicationServiceHelper
     */
    void sendMessage(ReplicationMessage message);
}
