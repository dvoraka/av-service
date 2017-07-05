package dvoraka.avservice.storage.configuration

import dvoraka.avservice.storage.replication.ReplicationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * AMQP replication service configuration spec.
 */
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'replication', 'client', 'amqp', 'no-db'])
@DirtiesContext
class AmqpReplicationServiceConfigISpec extends Specification {

    @Autowired
    ReplicationService service


    def cleanupSpec() {
        sleep(1_000)
    }

    def "test"() {
        expect:
            true
    }
}
