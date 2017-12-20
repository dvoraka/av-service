package dvoraka.avservice.client.transport.jms

import dvoraka.avservice.client.QueueCleanerISpec
import dvoraka.avservice.client.configuration.ClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [ClientConfig.class])
@PropertySource("classpath:avservice.properties")
@ActiveProfiles(['client', 'file-client', 'jms', 'no-db'])
@DirtiesContext
class JmsQueueCleanerISpec extends QueueCleanerISpec {

    @Autowired
    JmsTemplate jmsTemplate


    @Override
    def receive(String queueName) {
        return jmsTemplate.receive(queueName)
    }
}
