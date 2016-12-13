package dvoraka.avservice.server.amqp;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.ServerComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
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

    private final RabbitTemplate rabbitTemplate;
    private final MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(AmqpComponent.class.getName());
    public static final String ROUTING_KEY = "ROUTINGKEY";

    private final String responseExchange;
    private final String serviceId;
    private final List<AvMessageListener> listeners = new ArrayList<>();
    private final MessageConverter messageConverter;


    @Autowired
    public AmqpComponent(
            String responseExchange,
            String serviceId,
            RabbitTemplate rabbitTemplate,
            MessageInfoService messageInfoService
    ) {
        this.responseExchange = requireNonNull(responseExchange);
        this.serviceId = requireNonNull(serviceId);
        this.rabbitTemplate = rabbitTemplate;
        this.messageInfoService = messageInfoService;
        messageConverter = requireNonNull(rabbitTemplate.getMessageConverter());
    }

    @Override
    public void onMessage(Message message) {
        requireNonNull(message, "Message must not be null!");

        AvMessage avMessage;
        try {
            avMessage = (AvMessage) messageConverter.fromMessage(message);

            messageInfoService.save(avMessage, AvMessageSource.AMQP_COMPONENT_IN, serviceId);
        } catch (MessageConversionException e) {
            log.warn("Conversion error!", e);

            return;
        }

        synchronized (listeners) {
            notifyListeners(listeners, avMessage);
        }
    }

    @Override
    public void sendAvMessage(AvMessage message) {
        requireNonNull(message, "Message must not be null!");

        // TODO: improve exception handling
        try {
            rabbitTemplate.convertAndSend(responseExchange, ROUTING_KEY, message);
            messageInfoService.save(message, AvMessageSource.AMQP_COMPONENT_OUT, serviceId);
        } catch (AmqpException e) {
            log.warn("Message send problem!", e);

            // create error response
            AvMessage errorResponse = message.createErrorResponse(e.getMessage());

            rabbitTemplate.convertAndSend(responseExchange, ROUTING_KEY, errorResponse);
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
