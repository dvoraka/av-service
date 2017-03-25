package dvoraka.avservice.server.configuration.amqp

import dvoraka.avservice.server.checker.PerformanceTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@DirtiesContext
@ActiveProfiles(['client', 'amqp', 'amqp-client', 'amqp-checker', 'no-db'])
class AmqpCheckerConfigISpec extends Specification {

    @Autowired
    PerformanceTester loadTester


    def "test"() {
        expect:
            true
    }
}
