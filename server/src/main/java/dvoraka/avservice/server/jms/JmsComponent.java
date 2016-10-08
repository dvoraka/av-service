package dvoraka.avservice.server.jms;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.ServerComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

/**
 * JMS component.
 */
public class JmsComponent implements ServerComponent {

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private MessageConverter messageConverter;
    @Autowired
    private MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(JmsComponent.class.getName());
    private static final AvMessageSource MESSAGE_SOURCE = AvMessageSource.JMS_COMPONENT;

    private final String responseDestination;
    private final String serviceId;
    private final List<AvMessageListener> listeners = new ArrayList<>();


    public JmsComponent(String responseDestination, String serviceId) {
        this.responseDestination = responseDestination;
        this.serviceId = serviceId;
    }

    @Override
    public void onMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message may not be null!");
        }

        AvMessage avMessage;
        try {
            avMessage = (AvMessage) messageConverter.fromMessage(message);
            messageInfoService.save(avMessage, MESSAGE_SOURCE, serviceId);
        } catch (JMSException | MessageConversionException e) {
            log.warn("Conversion error!", e);

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

        jmsTemplate.convertAndSend(responseDestination, message);
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
     * AMQP method.
     *
     * @param message
     */
    @Override
    public void onMessage(org.springframework.amqp.core.Message message) {
        throw new UnsupportedOperationException();
    }
}
