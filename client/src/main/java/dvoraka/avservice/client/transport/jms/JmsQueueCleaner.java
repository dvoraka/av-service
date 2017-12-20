package dvoraka.avservice.client.transport.jms;

import dvoraka.avservice.client.util.QueueCleaner;
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
    public void clean(String name) {
        while (true) {
            if (jmsTemplate.receive(name) == null) {
                break;
            }
        }
    }
}
