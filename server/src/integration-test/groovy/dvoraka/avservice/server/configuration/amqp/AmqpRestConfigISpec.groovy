package dvoraka.avservice.server.configuration.amqp

import dvoraka.avservice.client.ServerComponent
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
@ActiveProfiles(['core', 'client', 'amqp', 'amqp-client', 'no-db'])
class AmqpRestConfigISpec extends Specification {

    @Autowired
    ServerComponent serverComponent


    def "test"() {
        expect:
            true
    }
}
