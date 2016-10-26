package dvoraka.avservice.server.configuration

import dvoraka.avservice.server.ServerComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [JmsCommonConfig.class])
@ActiveProfiles(["jms", "no-db"])
class JmsCommonConfigISpec extends Specification {

    @Autowired
    JmsTemplate jmsTemplate


    def "test"() {
        expect:
            true
    }
}
