package dvoraka.avservice.client;

import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.ReplicationMessage;

import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Component for sending a receiving replication messages.
 */
public interface ReplicationComponent extends
        MessageListener,
        org.springframework.amqp.core.MessageListener {

    void sendMessage(ReplicationMessage message);

    void addReplicationMessageListener(ReplicationMessageListener listener);

    void removeReplicationMessageListener(ReplicationMessageListener listener);

    @Override
    default void onMessage(Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void onMessage(org.springframework.amqp.core.Message message) {
        throw new UnsupportedOperationException();
    }
}
