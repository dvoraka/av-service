package dvoraka.avservice.client

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.client.util.QueueCleaner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Subject

@ContextConfiguration(classes = [ClientConfig.class])
@PropertySource("classpath:avservice.properties")
@ActiveProfiles(['replication', 'client', 'amqp', 'no-db'])
@DirtiesContext
class AmqpReplicationQueueCleanerISpec extends Specification {

    @Subject
    @Autowired
    QueueCleaner queueCleaner

    @Value('${avservice.amqp.replicationQueue}')
    String queueName


    def "clean queue"() {
        expect:
            queueCleaner.clean(queueName)
    }
}
