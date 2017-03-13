package dvoraka.avservice.client;

import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.ReplicationMessage;

/**
 * Component for sending a receiving replication messages.
 */
public interface ReplicationComponent {

    void sendMessage(ReplicationMessage message);

    void addReplicationMessageListener(ReplicationMessageListener listener);

    void removeReplicationMessageListener(ReplicationMessageListener listener);
}
