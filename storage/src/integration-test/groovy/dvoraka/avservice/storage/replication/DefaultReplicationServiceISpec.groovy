package dvoraka.avservice.storage.replication

import dvoraka.avservice.storage.configuration.StorageConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Default replication service spec.
 */
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'db'])
class DefaultReplicationServiceISpec extends Specification {

    @Autowired
    DefaultReplicationService service


    def "test"() {
        expect:
            true
    }
}
