package dvoraka.avservice.client;

import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.listener.ReplicationMessageListener;

/**
 * Component for sending and receiving replication messages.
 */
public interface ReplicationComponent extends
        GenericNetworkComponent<ReplicationMessage, ReplicationMessageListener> {
}
