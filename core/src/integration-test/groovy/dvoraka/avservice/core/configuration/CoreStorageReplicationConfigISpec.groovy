package dvoraka.avservice.core.configuration

import dvoraka.avservice.core.MessageProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [CoreConfig.class])
@ActiveProfiles(['core', 'storage', 'replication', 'amqp', 'client', 'no-db'])
@DirtiesContext
class CoreStorageReplicationConfigISpec extends Specification {

    @Autowired
    MessageProcessor messageProcessor


    def "test"() {
        expect:
            true
    }
}
