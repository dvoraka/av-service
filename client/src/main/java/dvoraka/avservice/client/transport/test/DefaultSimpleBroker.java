package dvoraka.avservice.client.transport.test;

import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.listener.MessageListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


@Slf4j
public class DefaultSimpleBroker implements SimpleBroker<ReplicationMessage> {

    private final Map<String, Set<MessageListener<ReplicationMessage>>> queueMap;


    public DefaultSimpleBroker() {
        queueMap = new ConcurrentHashMap<>();
    }

    @Override
    public void send(String queueName, ReplicationMessage message) {
        log.debug("Send ({}): {}", queueName, message);

        if (queueMap.get(queueName) != null) {
            Set<MessageListener<ReplicationMessage>> listeners = queueMap.get(queueName);
            listeners.forEach(listener -> listener.onMessage(message));
        }
    }

    @Override
    public void addMessageListener(MessageListener<ReplicationMessage> listener, String queueName) {

        if (queueMap.get(queueName) == null) {
            Set<MessageListener<ReplicationMessage>> listeners = new CopyOnWriteArraySet<>();
            listeners.add(listener);
            queueMap.put(queueName, listeners);
        } else {
            Set<MessageListener<ReplicationMessage>> listeners = queueMap.get(queueName);
            listeners.add(listener);
        }
    }
}
