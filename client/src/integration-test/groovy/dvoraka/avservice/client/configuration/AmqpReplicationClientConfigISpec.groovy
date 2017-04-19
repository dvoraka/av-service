package dvoraka.avservice.client.configuration

import dvoraka.avservice.client.service.ReplicationServiceClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'replication', 'amqp', 'amqp-client', 'no-db'])
@DirtiesContext
class AmqpReplicationClientConfigISpec extends Specification {

    @Autowired
    ReplicationServiceClient client


    def "test"() {
        expect:
            true
    }
}
