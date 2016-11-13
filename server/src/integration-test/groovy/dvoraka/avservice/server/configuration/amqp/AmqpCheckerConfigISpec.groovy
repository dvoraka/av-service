package dvoraka.avservice.server.configuration.amqp

import dvoraka.avservice.server.checker.DefaultLoadTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(['amqp', 'amqp-checker', 'no-db'])
class AmqpCheckerConfigISpec extends Specification {

    @Autowired
    DefaultLoadTester loadTester


    def "test"() {
        expect:
            true
    }
}
