package dvoraka.avservice.storage.replication

import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification

/**
 * Default replication service spec.
 */
class DefaultReplicationServiceISpec extends Specification {

    @Autowired
    DefaultReplicationService service


    def "test"() {
        expect:
            true
    }
}
