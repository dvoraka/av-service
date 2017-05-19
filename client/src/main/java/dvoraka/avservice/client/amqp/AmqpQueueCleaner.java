package dvoraka.avservice.client.amqp;

import dvoraka.avservice.client.QueueCleaner;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AMQP queue cleaner implementation.
 */
@Component
public class AmqpQueueCleaner implements QueueCleaner {

    private final AmqpTemplate amqpTemplate;


    @Autowired
    public AmqpQueueCleaner(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public void clean(String queueName) {
        while (true) {
            if (amqpTemplate.receive(queueName) == null) {
                break;
            }
        }
    }
}
