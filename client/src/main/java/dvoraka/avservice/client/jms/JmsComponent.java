package dvoraka.avservice.client.jms;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.service.MessageInfoService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Objects.requireNonNull;

/**
 * JMS component.
 */
@Component
public class JmsComponent implements ServerComponent {

    private final JmsTemplate jmsTemplate;
    private final MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(JmsComponent.class);

    private final String destination;
    private final String serviceId;
    private final List<AvMessageListener> listeners;
    private final MessageConverter messageConverter;


    @Autowired
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public JmsComponent(String destination,
                        String serviceId,
                        JmsTemplate jmsTemplate,
                        MessageInfoService messageInfoService
    ) {
        this.destination = requireNonNull(destination);
        this.serviceId = requireNonNull(serviceId);
        this.jmsTemplate = requireNonNull(jmsTemplate);
        this.messageInfoService = requireNonNull(messageInfoService);
        messageConverter = requireNonNull(jmsTemplate.getMessageConverter());

        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void onMessage(Message message) {
        requireNonNull(message, "Message must not be null!");

        AvMessage avMessage;
        try {
            avMessage = (AvMessage) messageConverter.fromMessage(message);
            messageInfoService.save(avMessage, AvMessageSource.JMS_COMPONENT_IN, serviceId);
        } catch (JMSException | MessageConversionException e) {
            log.warn("Conversion error!", e);

            return;
        }

        notifyListeners(listeners, avMessage);
    }

    @Override
    public void sendAvMessage(AvMessage message) {
        requireNonNull(message, "Message must not be null!");

        try {
            jmsTemplate.convertAndSend(destination, message);
            messageInfoService.save(message, AvMessageSource.JMS_COMPONENT_OUT, serviceId);
        } catch (MessageConversionException e) {
            log.warn("Conversion problem!", e);

            String errorMessage = e.getMessage() == null ? "" : e.getMessage();
            AvMessage errorResponse = message.createErrorResponse(errorMessage);
            jmsTemplate.convertAndSend(destination, errorResponse);
        } catch (JmsException e) {
            log.warn("Message send problem!", e);
        }
    }

    @Override
    public void addAvMessageListener(AvMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeAvMessageListener(AvMessageListener listener) {
        listeners.remove(listener);
    }

    public int listenersCount() {
        return listeners.size();
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }
}
