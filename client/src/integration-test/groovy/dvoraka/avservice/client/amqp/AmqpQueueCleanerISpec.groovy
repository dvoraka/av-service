package dvoraka.avservice.client.amqp

import dvoraka.avservice.client.QueueCleanerISpec
import dvoraka.avservice.client.configuration.ClientConfig
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [ClientConfig.class])
@PropertySource("classpath:avservice.properties")
@ActiveProfiles(['client', 'file-client', 'amqp', 'no-db'])
@DirtiesContext
class AmqpQueueCleanerISpec extends QueueCleanerISpec {

    @Autowired
    AmqpTemplate amqpTemplate


    @Override
    def receive(String queueName) {
        return amqpTemplate.receive(queueName)
    }
}
