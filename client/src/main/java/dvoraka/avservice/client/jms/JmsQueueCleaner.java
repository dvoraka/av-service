package dvoraka.avservice.client.jms;

import dvoraka.avservice.client.QueueCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * JMS queue/destination cleaner implementation.
 */
@Component
public class JmsQueueCleaner implements QueueCleaner {

    private final JmsTemplate jmsTemplate;


    @Autowired
    public JmsQueueCleaner(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void clean(String queueName) {
        while (true) {
            if (jmsTemplate.receive(queueName) == null) {
                break;
            }
        }
    }
}
