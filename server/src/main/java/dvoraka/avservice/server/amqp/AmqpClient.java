package dvoraka.avservice.server.amqp;

import dvoraka.avservice.common.data.AvMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Testing AMQP client.
 */
@Component
public class AmqpClient {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private MessageConverter messageConverter;

    private static final String DEFAULT_ROUTING_KEY = "RoutingKey";

    private String defaultExchange;


    public AmqpClient(String defaultExchange) {
        this.defaultExchange = defaultExchange;
    }

    @PostConstruct
    private void init() {
        rabbitTemplate.setMessageConverter(messageConverter);
    }


    public void sendMessage(AvMessage message, String exchange) {
        rabbitTemplate.convertAndSend(exchange, DEFAULT_ROUTING_KEY, message);
    }

    public AvMessage receiveMessage() {
        return (AvMessage) rabbitTemplate.receiveAndConvert();
    }

    public String getDefaultExchange() {
        return defaultExchange;
    }

    public void setDefaultExchange(String defaultExchange) {
        this.defaultExchange = defaultExchange;
    }
}
