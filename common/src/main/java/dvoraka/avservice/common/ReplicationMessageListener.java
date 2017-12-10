package dvoraka.avservice.common;

import dvoraka.avservice.common.data.replication.ReplicationMessage;

/**
 * Replication message listener.
 */
@FunctionalInterface
public interface ReplicationMessageListener extends MessageListener<ReplicationMessage> {
}
