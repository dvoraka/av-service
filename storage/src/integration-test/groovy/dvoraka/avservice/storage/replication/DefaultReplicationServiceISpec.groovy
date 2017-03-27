package dvoraka.avservice.storage.replication

import dvoraka.avservice.common.Utils
import dvoraka.avservice.storage.configuration.StorageConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Default replication service spec.
 */
@ContextConfiguration(classes = [StorageConfig.class])
@ActiveProfiles(['storage', 'replication', 'client', 'amqp', 'amqp-client', 'db'])
class DefaultReplicationServiceISpec extends Specification {

    @Autowired
    DefaultReplicationService service


    def "test"() {
        expect:
            true
    }

    @Ignore
    def "save file"() {
        expect:
            service.saveFile(Utils.genSaveMessage())
    }
}
