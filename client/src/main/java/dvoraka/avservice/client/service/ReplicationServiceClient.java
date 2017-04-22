package dvoraka.avservice.client.service;

import dvoraka.avservice.common.data.ReplicationMessage;

/**
 * Replication service client. It is used for client connections to the replication service.
 */
@FunctionalInterface
public interface ReplicationServiceClient {

    void sendMessage(ReplicationMessage message);
}
