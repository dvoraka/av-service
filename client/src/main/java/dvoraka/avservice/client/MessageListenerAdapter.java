package dvoraka.avservice.client;

import dvoraka.avservice.common.data.AvMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import javax.jms.Message;

/**
 * Message listener "adapter" for various types with a default implementation.
 */
public interface MessageListenerAdapter extends
        javax.jms.MessageListener,
        org.springframework.amqp.core.MessageListener,
        org.springframework.kafka.listener.MessageListener<String, AvMessage> {

    @Override
    default void onMessage(Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void onMessage(org.springframework.amqp.core.Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void onMessage(ConsumerRecord<String, AvMessage> record) {
        throw new UnsupportedOperationException();
    }
}
