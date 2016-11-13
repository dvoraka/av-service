package dvoraka.avservice.server.configuration.jms

import dvoraka.avservice.server.checker.DefaultLoadTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(['jms', 'jms-checker', 'no-db'])
class JmsCheckerConfigISpec extends Specification {

    @Autowired
    DefaultLoadTester loadTester


    def "test"() {
        expect:
            true
    }
}
