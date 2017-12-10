package dvoraka.avservice.client;

/**
 * Component for sending and receiving replication messages.
 */
public interface ReplicationComponent extends
        ReplicationMessageReceiver,
        ReplicationMessageSender,
        MessageListenerAdapter {
}
