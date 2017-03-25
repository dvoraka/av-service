package dvoraka.avservice.client.checker

import dvoraka.avservice.client.configuration.ClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [ClientConfig.class])
@DirtiesContext
@ActiveProfiles(['client', 'amqp', 'amqp-client', 'checker', 'no-db'])
class AmqpCheckerConfigISpec extends Specification {

    @Autowired
    PerformanceTester loadTester


    def "test"() {
        expect:
            true
    }
}
