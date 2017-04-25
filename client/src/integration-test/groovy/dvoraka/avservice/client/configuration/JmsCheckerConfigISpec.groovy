package dvoraka.avservice.client.configuration

import dvoraka.avservice.client.checker.PerformanceTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(['client', 'jms', 'file-client', 'checker', 'no-db'])
@DirtiesContext
class JmsCheckerConfigISpec extends Specification {

    @Autowired
    PerformanceTester loadTester


    def "test"() {
        expect:
            true
    }
}
