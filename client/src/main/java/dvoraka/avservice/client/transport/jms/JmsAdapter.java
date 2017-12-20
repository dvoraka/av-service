package dvoraka.avservice.client.transport.jms;

import dvoraka.avservice.client.transport.AbstractNetworkComponent;
import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.InfoSource;
import dvoraka.avservice.common.listener.AvMessageListener;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

import static java.util.Objects.requireNonNull;

/**
 * JMS network component adapter.
 */
@Component
public class JmsAdapter extends AbstractNetworkComponent<AvMessage, AvMessageListener>
        implements AvNetworkComponent {

    private final JmsTemplate jmsTemplate;
    private final MessageInfoService messageInfoService;

    private final String destination;
    private final String serviceId;
    private final MessageConverter messageConverter;


    @Autowired
    public JmsAdapter(
            String destination,
            String serviceId,
            JmsTemplate jmsTemplate,
            MessageInfoService messageInfoService
    ) {
        this.destination = requireNonNull(destination);
        this.serviceId = requireNonNull(serviceId);
        this.jmsTemplate = requireNonNull(jmsTemplate);
        this.messageInfoService = requireNonNull(messageInfoService);
        messageConverter = requireNonNull(jmsTemplate.getMessageConverter());
    }

    @Override
    public void onMessage(Message message) {
        requireNonNull(message, "Message must not be null!");
        log.debug("On message: {}", message);

        AvMessage avMessage;
        try {
            avMessage = (AvMessage) messageConverter.fromMessage(message);
            messageInfoService.save(avMessage, InfoSource.JMS_ADAPTER_IN, serviceId);
        } catch (JMSException | MessageConversionException e) {
            log.warn("Conversion error!", e);

            return;
        }

        notifyListeners(getListeners(), avMessage);
    }

    @Override
    public void sendMessage(AvMessage message) {
        requireNonNull(message, "Message must not be null!");
        log.debug("Send: {}", message);

        try {
            jmsTemplate.convertAndSend(destination, message);
            messageInfoService.save(message, InfoSource.JMS_ADAPTER_OUT, serviceId);
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
    public String getServiceId() {
        return serviceId;
    }
}
