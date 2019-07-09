package dvoraka.avservice.client;

import dvoraka.avservice.common.data.Message;
import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.listener.MessageListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;


/**
 * Message listener "adapter" for various types with a default implementation.
 */
public interface MessageListenerAdapter<M extends Message> extends
        javax.jms.MessageListener,
        org.springframework.amqp.core.MessageListener,
        org.springframework.kafka.listener.MessageListener<String, M>,
        MessageListener<ReplicationMessage> {

    @Override
    default void onMessage(javax.jms.Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void onMessage(org.springframework.amqp.core.Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void onMessage(ConsumerRecord<String, M> record) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void onMessage(ReplicationMessage message) {
        throw new UnsupportedOperationException();
    }
}
