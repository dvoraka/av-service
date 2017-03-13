package dvoraka.avservice.client;

import dvoraka.avservice.common.ReplicationMessageListener;

/**
 * Component for sending a receiving replication messages.
 */
public interface ReplicationComponent {

    void addReplicationMessageListener(ReplicationMessageListener listener);

    void removeReplicationMessageListener(ReplicationMessageListener listener);
}
