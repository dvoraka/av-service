package dvoraka.avservice.client.service;

import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.helper.replication.ReplicationHelper;
import dvoraka.avservice.common.helper.replication.ReplicationServiceHelper;

/**
 * Replication service client. It is used for client connections to the replication service.
 */
@FunctionalInterface
public interface ReplicationServiceClient {

    /**
     * Sends a replication message.
     *
     * @param message the message
     * @see ReplicationHelper
     * @see ReplicationServiceHelper
     */
    void sendMessage(ReplicationMessage message);
}
