package dvoraka.avservice.server.configuration.amqp

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(["amqp", "no-db"])
class AmqpConfigISpec extends Specification {

    def "test"() {
        expect:
            true
    }
}
