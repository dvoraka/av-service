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
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * JMS component.
 */
@Component
public class JmsComponent implements ServerComponent {

    private final JmsTemplate jmsTemplate;
    private final MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(JmsComponent.class.getName());

    private final String responseDestination;
    private final String serviceId;
    private final List<AvMessageListener> listeners = new ArrayList<>();


    @Autowired
    public JmsComponent(String responseDestination,
                        String serviceId,
                        JmsTemplate jmsTemplate,
                        MessageInfoService messageInfoService
    ) {
        this.responseDestination = responseDestination;
        this.serviceId = serviceId;
        this.messageInfoService = messageInfoService;
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void onMessage(Message message) {
        requireNonNull(message, "Message must not be null!");

        AvMessage avMessage;
        try {
            avMessage = (AvMessage) jmsTemplate.getMessageConverter().fromMessage(message);
            messageInfoService.save(avMessage, AvMessageSource.JMS_COMPONENT_IN, serviceId);
        } catch (JMSException | MessageConversionException e) {
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

        jmsTemplate.convertAndSend(responseDestination, message);
        messageInfoService.save(message, AvMessageSource.JMS_COMPONENT_OUT, serviceId);
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
