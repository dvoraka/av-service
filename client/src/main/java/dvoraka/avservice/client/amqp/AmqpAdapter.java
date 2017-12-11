package dvoraka.avservice.client.amqp;

import dvoraka.avservice.client.transport.AbstractNetworkComponent;
import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.InfoSource;
import dvoraka.avservice.common.listener.AvMessageListener;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * AMQP network component adapter.
 */
@Component
public class AmqpAdapter extends AbstractNetworkComponent<AvMessage, AvMessageListener>
        implements AvNetworkComponent {

    private final String responseExchange;
    private final String serviceId;
    private final RabbitTemplate rabbitTemplate;
    private final MessageInfoService messageInfoService;

    public static final String ROUTING_KEY = "ROUTINGKEY";

    private final MessageConverter messageConverter;


    @Autowired
    public AmqpAdapter(
            String responseExchange,
            String serviceId,
            RabbitTemplate rabbitTemplate,
            MessageInfoService messageInfoService
    ) {
        this.responseExchange = requireNonNull(responseExchange);
        this.serviceId = requireNonNull(serviceId);
        this.rabbitTemplate = requireNonNull(rabbitTemplate);
        this.messageInfoService = requireNonNull(messageInfoService);
        messageConverter = requireNonNull(rabbitTemplate.getMessageConverter());
    }

    @Override
    public void onMessage(Message message) {
        requireNonNull(message, "Message must not be null!");
        log.debug("On message: {}", message);

        AvMessage avMessage;
        try {
            avMessage = (AvMessage) messageConverter.fromMessage(message);
            messageInfoService.save(avMessage, InfoSource.AMQP_ADAPTER_IN, serviceId);
        } catch (MessageConversionException e) {
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
            rabbitTemplate.convertAndSend(responseExchange, ROUTING_KEY, message);
            messageInfoService.save(message, InfoSource.AMQP_ADAPTER_OUT, serviceId);
        } catch (MessageConversionException e) {
            log.warn("Conversion problem!", e);

            String errorMessage = e.getMessage() == null ? "" : e.getMessage();
            AvMessage errorResponse = message.createErrorResponse(errorMessage);
            rabbitTemplate.convertAndSend(responseExchange, ROUTING_KEY, errorResponse);
        } catch (AmqpException e) {
            log.warn("Message send problem!", e);
        }
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }
}
