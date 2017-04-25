package dvoraka.avservice.server.configuration.jms

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(['jms', 'no-db'])
@DirtiesContext
@Ignore("broken")
class JmsConfigISpec extends Specification {

    @Autowired
    JmsTemplate jmsTemplate


    def "test"() {
        expect:
            true
    }
}
