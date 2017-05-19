package dvoraka.avservice.client.amqp

import dvoraka.avservice.client.QueueCleanerISpec
import dvoraka.avservice.client.configuration.ClientConfig
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [ClientConfig.class])
@PropertySource("classpath:avservice.properties")
@ActiveProfiles(['client', 'file-client', 'amqp', 'no-db'])
class AmqpQueueCleanerISpec extends QueueCleanerISpec {
}
