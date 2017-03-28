package dvoraka.avservice.client.amqp;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.ReplicationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.util.Objects.requireNonNull;

/**
 * AMQP component for the replication service.
 */
@Component
public class AmqpReplicationComponent implements ReplicationComponent {

    private final RabbitTemplate rabbitTemplate;

    private static final Logger log = LogManager.getLogger(AmqpReplicationComponent.class);

    private final Set<ReplicationMessageListener> listeners;


    @Autowired
    public AmqpReplicationComponent(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = requireNonNull(rabbitTemplate);
        listeners = new CopyOnWriteArraySet<>();
    }

    @Override
    public void onMessage(Message message) {
        log.debug("Receive: " + message);
    }

    @Override
    public void sendMessage(ReplicationMessage message) {
        log.debug("Send: " + message);
        rabbitTemplate.convertAndSend(message);
    }

    @Override
    public void addReplicationMessageListener(ReplicationMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeReplicationMessageListener(ReplicationMessageListener listener) {
        listeners.remove(listener);
    }
}
