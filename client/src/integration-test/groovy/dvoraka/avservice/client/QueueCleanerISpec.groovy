package dvoraka.avservice.client

import dvoraka.avservice.client.transport.AvNetworkComponent
import dvoraka.avservice.client.util.QueueCleaner
import dvoraka.avservice.common.util.Utils
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
    AvNetworkComponent networkComponent

    @Value('${avservice.amqp.fileQueue}')
    String queueName


    def "fill and clean queue"() {
        given:
            10.times {
                networkComponent.sendMessage(Utils.genMessage())
            }

        when:
            queueCleaner.clean(queueName)

        then:
            receive(queueName) == null
    }

    /**
     * Receives messages from a queue/destination.
     *
     * @param queueName the queue/destination name
     */
    def receive(String queueName) {
        // you need to override it
    }
}
