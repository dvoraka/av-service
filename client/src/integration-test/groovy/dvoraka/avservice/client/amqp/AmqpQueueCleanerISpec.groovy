package dvoraka.avservice.client.amqp

import dvoraka.avservice.client.QueueCleaner
import dvoraka.avservice.client.ServerComponent
import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.common.Utils
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [ClientConfig.class])
@PropertySource("classpath:avservice.properties")
@ActiveProfiles(['client', 'file-client', 'amqp', 'no-db'])
class AmqpQueueCleanerISpec extends Specification {

    @Autowired
    QueueCleaner queueCleaner

    @Autowired
    ServerComponent serverComponent
    @Autowired
    AmqpTemplate amqpTemplate

    @Value('${avservice.amqp.fileQueue}')
    String queueName


    def "fill and clean queue"() {
        given:
            10.times {
                serverComponent.sendAvMessage(Utils.genMessage())
            }

        when:
            queueCleaner.clean(queueName)

        then:
            amqpTemplate.receive(queueName) == null
    }
}
