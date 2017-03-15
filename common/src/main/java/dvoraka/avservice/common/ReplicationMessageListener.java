package dvoraka.avservice.common;

import dvoraka.avservice.common.data.ReplicationMessage;

/**
 * Replication message listener.
 */
@FunctionalInterface
public interface ReplicationMessageListener {

    void onMessage(ReplicationMessage message);
}
