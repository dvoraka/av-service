package dvoraka.avservice.common;

import dvoraka.avservice.common.data.ReplicationMessage;

/**
 * Replication message listener.
 */
@FunctionalInterface
public interface ReplicationMessageListener {

    /**
     * Receives replication messages.
     *
     * @param message the message
     */
    void onMessage(ReplicationMessage message);
}
