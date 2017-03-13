package dvoraka.avservice.common;

import javax.jms.Message;

/**
 * Message listener "adapter" for various types with a default implementation.
 */
public interface MessageListenerAdapter extends
        javax.jms.MessageListener,
        org.springframework.amqp.core.MessageListener {

    @Override
    default void onMessage(Message message) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void onMessage(org.springframework.amqp.core.Message message) {
        throw new UnsupportedOperationException();
    }
}
