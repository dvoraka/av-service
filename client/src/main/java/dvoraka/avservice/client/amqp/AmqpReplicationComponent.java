package dvoraka.avservice.client.amqp;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.common.ReplicationMessageListener;
import dvoraka.avservice.common.data.ReplicationMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
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
    private final String nodeId;

    private static final Logger log = LogManager.getLogger(AmqpReplicationComponent.class);

    private final MessageConverter messageConverter;
    private final Set<ReplicationMessageListener> listeners;


    @Autowired
    public AmqpReplicationComponent(RabbitTemplate rabbitTemplate, String nodeId) {
        this.rabbitTemplate = requireNonNull(rabbitTemplate);
        this.nodeId = requireNonNull(nodeId);

        messageConverter = requireNonNull(rabbitTemplate.getMessageConverter());
        listeners = new CopyOnWriteArraySet<>();
    }

    @Override
    public void onMessage(Message message) {
        log.debug("Receive: " + message);

        ReplicationMessage replicationMessage;
        try {
            replicationMessage = (ReplicationMessage) messageConverter.fromMessage(message);
            log.debug("Converted ({}): {}", nodeId, replicationMessage);
        } catch (MessageConversionException e) {
            log.warn("Conversion error!", e);

            return;
        }

        listeners.forEach(listener -> listener.onMessage(replicationMessage));
    }

    @Override
    public void sendMessage(ReplicationMessage message) {
        log.debug("Send ({}): {}", nodeId, message);
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
