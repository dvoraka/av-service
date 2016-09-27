package dvoraka.avservice.server.amqp;

import dvoraka.avservice.common.data.AvMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Testing AMQP client.
 */
public class AmqpClient {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void sendMessage(AvMessage message, String exchange) {
        rabbitTemplate.convertAndSend(exchange, message);
    }
}
