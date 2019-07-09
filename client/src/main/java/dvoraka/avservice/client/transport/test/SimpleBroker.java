package dvoraka.avservice.client.transport.test;

import dvoraka.avservice.common.data.Message;
import dvoraka.avservice.common.listener.MessageListener;

public interface SimpleBroker<M extends Message> {

    void send(String queueName, M message);

    void addMessageListener(MessageListener<M> listener, String queueName);
}
