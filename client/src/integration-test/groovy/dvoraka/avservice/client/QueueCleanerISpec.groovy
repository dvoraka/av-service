package dvoraka.avservice.client

import dvoraka.avservice.common.Utils
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject

@Ignore('base class')
class QueueCleanerISpec extends Specification {

    @Subject
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
