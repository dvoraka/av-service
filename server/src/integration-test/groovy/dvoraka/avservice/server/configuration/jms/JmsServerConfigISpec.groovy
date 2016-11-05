package dvoraka.avservice.server.configuration.jms

import dvoraka.avservice.server.AvServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(['core', 'jms', 'jms-server', 'no-db'])
class JmsServerConfigISpec extends Specification {

    @Autowired
    AvServer avServer


    def "test"() {
        expect:
            true
    }
}
