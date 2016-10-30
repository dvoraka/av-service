package dvoraka.avservice.server.configuration.amqp

import dvoraka.avservice.server.AvServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(["amqp", "no-db"])
class AmqpConfigISpec extends Specification {

    @Autowired
    AvServer avServer


    def "test"() {
        expect:
            true
    }
}
