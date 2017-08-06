package dvoraka.avservice.client;

import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.replication.ReplicationMessage;

/**
 * Component for sending and receiving replication messages.
 */
public interface ReplicationComponent extends MessageListenerAdapter {

    /**
     * Sends a replication message.
     *
     * @param message the message
     */
    void sendMessage(ReplicationMessage message);

    /**
     * Adds a message listener.
     *
     * @param listener the listener
     */
    void addReplicationMessageListener(ReplicationMessageListener listener);

    /**
     * Removes a message listener.
     *
     * @param listener the listener
     */
    void removeReplicationMessageListener(ReplicationMessageListener listener);
}
