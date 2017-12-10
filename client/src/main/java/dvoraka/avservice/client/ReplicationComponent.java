package dvoraka.avservice.client;

import dvoraka.avservice.common.data.replication.ReplicationMessage;

/**
 * Component for sending and receiving replication messages.
 */
public interface ReplicationComponent extends MessageListenerAdapter, ReplicationMessageReceiver {

    /**
     * Sends a replication message.
     *
     * @param message the message
     */
    void sendMessage(ReplicationMessage message);
}
