package dvoraka.avservice.server.configuration.jms

import dvoraka.avservice.server.checker.PerformanceTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [JmsConfig.class])
@ActiveProfiles(['client', 'jms', 'jms-checker', 'no-db'])
@DirtiesContext
class JmsCheckerConfigISpec extends Specification {

    @Autowired
    PerformanceTester loadTester


    def "test"() {
        expect:
            true
    }
}
