package dvoraka.avservice.server.configuration.jms

import dvoraka.avservice.client.ServerComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(['core', 'client', 'jms', 'jms-client', 'no-db'])
@DirtiesContext
class JmsRestConfigISpec extends Specification {

    @Autowired
    ServerComponent serverComponent


    def "test"() {
        expect:
            true
    }
}
