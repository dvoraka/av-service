package dvoraka.avservice.server.configuration.jms

import dvoraka.avservice.server.jms.JmsClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Configuration test.
 */
@Ignore("needs redesign")
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(["jms", "jms-client"])
class JmsClientConfigISpec extends Specification {

    @Autowired
    JmsClient jmsClient


    def "test"() {
        expect:
            true
    }
}
