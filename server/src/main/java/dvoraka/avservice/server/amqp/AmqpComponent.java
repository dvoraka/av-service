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
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * AMQP component.
 */
public class AmqpComponent implements ServerComponent {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(AmqpComponent.class.getName());
    private static final AvMessageSource MESSAGE_SOURCE = AvMessageSource.AMQP_COMPONENT;

    private final String responseExchange;
    private final String serviceId;
    private final List<AvMessageListener> listeners = new ArrayList<>();


    public AmqpComponent(String responseExchange, String serviceId) {
        this.responseExchange = responseExchange;
        this.serviceId = serviceId;
    }

    @Override
    public void onMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message may not be null!");
        }

        AvMessage avMessage;
        try {
            avMessage = AvMessageMapper.transform(message);
            messageInfoService.save(avMessage, MESSAGE_SOURCE, serviceId);
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
        if (message == null) {
            throw new IllegalArgumentException("Message may not be null!");
        }

        Message response = null;
        try {
            response = AvMessageMapper.transform(message);
            rabbitTemplate.send(responseExchange, "ROUTINGKEY", response);
        } catch (MapperException e) {
            log.warn("Message transformation problem!", e);
            // create error response
            AvMessage errorResponse = message.createErrorResponse(e.getMessage());
            try {
                response = AvMessageMapper.transform(errorResponse);
            } catch (MapperException e1) {
                log.error("Message send error!", e1);
            }
            rabbitTemplate.send(responseExchange, "ROUTINGKEY", response);
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
