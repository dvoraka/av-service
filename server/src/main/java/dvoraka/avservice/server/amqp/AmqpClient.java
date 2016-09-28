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


    @PostConstruct
    private void init() {
        rabbitTemplate.setMessageConverter(messageConverter);
    }


    public void sendMessage(AvMessage message, String exchange) {
        rabbitTemplate.convertAndSend(exchange, "RoutingKey", message);
    }
}
