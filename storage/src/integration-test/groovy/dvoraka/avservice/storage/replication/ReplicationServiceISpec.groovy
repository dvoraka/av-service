package dvoraka.avservice.storage.replication

import dvoraka.avservice.client.QueueCleaner
import dvoraka.avservice.common.Utils
import dvoraka.avservice.storage.configuration.StorageConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * AMQP replication service spec.
 *
 * Complete test is in the server module. This could be for some testing without a server
 * infrastructure.
 */
@ContextConfiguration(classes = [StorageConfig.class])
@PropertySource("classpath:avservice.properties")
@ActiveProfiles(['storage', 'replication', 'client', 'amqp', 'no-db'])
class ReplicationServiceISpec extends Specification {

    @Autowired
    ReplicationService service

    @Autowired
    QueueCleaner queueCleaner

    @Value('${avservice.amqp.replicationQueue}')
    String queueName


    def cleanup() {
        // clean the replication queue
        queueCleaner.clean(queueName)
    }

    def "test"() {
        expect:
            true
    }

    @Ignore("manual testing")
    def "save file"() {
        expect:
            service.saveFile(Utils.genSaveMessage())
    }
}
