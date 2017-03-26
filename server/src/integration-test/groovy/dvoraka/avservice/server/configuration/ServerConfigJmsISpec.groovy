package dvoraka.avservice.server.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [ServerConfig.class])
@DirtiesContext
@ActiveProfiles(['server', 'client', 'jms', 'jms-client', 'no-db'])
class ServerConfigJmsISpec extends Specification {

    @Autowired
    JmsTemplate jmsTemplate


    def "test"() {
        expect:
            true
    }
}
