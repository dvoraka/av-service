package dvoraka.avservice.server.configuration.jms

import dvoraka.avservice.server.AvServer
import dvoraka.avservice.server.configuration.ServerConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [ServerConfig.class])
@ActiveProfiles(['core', 'check', 'server', 'jms', 'jms-server', 'no-db'])
@DirtiesContext
class JmsServerConfigISpec extends Specification {

    @Autowired
    AvServer avServer


    def "test"() {
        expect:
            true
    }
}
