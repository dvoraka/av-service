package dvoraka.avservice.common.listener;

import dvoraka.avservice.common.data.replication.ReplicationMessage;

/**
 * Replication message listener.
 */
@FunctionalInterface
public interface ReplicationMessageListener extends MessageListener<ReplicationMessage> {
}
