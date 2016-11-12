package dvoraka.avservice.server.amqp;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.amqp.AvMessageMapper;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.common.exception.MapperException;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.ServerComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * AMQP component.
 */
@Component
public class AmqpComponent implements ServerComponent {

    private final AmqpTemplate amqpTemplate;
    private final MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(AmqpComponent.class.getName());
    public static final String ROUTING_KEY = "ROUTINGKEY";

    private final String responseExchange;
    private final String serviceId;
    private final List<AvMessageListener> listeners = new ArrayList<>();


    @Autowired
    public AmqpComponent(
            String responseExchange,
            String serviceId,
            AmqpTemplate amqpTemplate,
            MessageInfoService messageInfoService
    ) {
        this.responseExchange = responseExchange;
        this.serviceId = serviceId;
        this.amqpTemplate = amqpTemplate;
        this.messageInfoService = messageInfoService;
    }

    @Override
    public void onMessage(Message message) {
        requireNonNull(message, "Message must not be null!");

        AvMessage avMessage;
        try {
            avMessage = AvMessageMapper.transform(message);
            messageInfoService.save(avMessage, AvMessageSource.AMQP_COMPONENT_IN, serviceId);
        } catch (MapperException e) {
            log.warn("Transformation error!", e);

            return;
        }

        synchronized (listeners) {
            notifyListeners(listeners, avMessage);
        }
    }

    @Override
    public void sendMessage(AvMessage message) {
        requireNonNull(message, "Message must not be null!");

        // TODO: improve exception handling
        try {
            amqpTemplate.convertAndSend(responseExchange, ROUTING_KEY, message);
            messageInfoService.save(message, AvMessageSource.AMQP_COMPONENT_OUT, serviceId);
        } catch (AmqpException e) {
            log.warn("Message send problem!", e);

            // create error response
            AvMessage errorResponse = message.createErrorResponse(e.getMessage());

            amqpTemplate.convertAndSend(responseExchange, ROUTING_KEY, errorResponse);
        }
    }

    @Override
    public void addAvMessageListener(AvMessageListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeAvMessageListener(AvMessageListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public int listenersCount() {
        return listeners.size();
    }

    /**
     * JMS method.
     *
     * @param message
     */
    @Override
    public void onMessage(javax.jms.Message message) {
        throw new UnsupportedOperationException();
    }
}
