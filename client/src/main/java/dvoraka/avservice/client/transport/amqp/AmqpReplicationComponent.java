package dvoraka.avservice.client.transport.amqp;

import dvoraka.avservice.client.transport.AbstractNetworkComponent;
import dvoraka.avservice.client.transport.ReplicationComponent;
import dvoraka.avservice.common.data.replication.MessageRouting;
import dvoraka.avservice.common.data.replication.ReplicationMessage;
import dvoraka.avservice.common.helper.MessageHelper;
import dvoraka.avservice.common.listener.ReplicationMessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * AMQP component for the replication service.
 */
@Component
public class AmqpReplicationComponent
        extends AbstractNetworkComponent<ReplicationMessage, ReplicationMessageListener>
        implements ReplicationComponent, MessageHelper {

    private final RabbitTemplate rabbitTemplate;
    private final String nodeId;
    private final String broadcastKey;

    private static final Logger log = LogManager.getLogger(AmqpReplicationComponent.class);

    private final MessageConverter messageConverter;


    @Autowired
    public AmqpReplicationComponent(
            RabbitTemplate rabbitTemplate,
            String nodeId,
            String broadcastKey
    ) {
        this.rabbitTemplate = requireNonNull(rabbitTemplate);
        this.nodeId = requireNonNull(nodeId);
        this.broadcastKey = requireNonNull(broadcastKey);

        messageConverter = requireNonNull(rabbitTemplate.getMessageConverter());
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

        notifyListeners(getListeners(), replicationMessage);
    }

    @Override
    public void send(ReplicationMessage message) {
        log.debug("Send ({}): {}", nodeId, message);
        if (message.getRouting() == MessageRouting.BROADCAST) {
            rabbitTemplate.convertAndSend(broadcastKey, message);
        } else {
            rabbitTemplate.convertAndSend(message.getToId(), message);
        }
    }

    @Override
    public String getServiceId() {
        return nodeId;
    }
}
