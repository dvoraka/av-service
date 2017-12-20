package dvoraka.avservice.client.transport.amqp;

import dvoraka.avservice.client.util.QueueCleaner;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * AMQP queue cleaner implementation.
 */
@Component
public class AmqpQueueCleaner implements QueueCleaner {

    private final AmqpTemplate amqpTemplate;


    @Autowired
    public AmqpQueueCleaner(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = requireNonNull(amqpTemplate);
    }

    @Override
    public void clean(String name) {
        while (true) {
            if (amqpTemplate.receive(name) == null) {
                break;
            }
        }
    }
}
