package dvoraka.avservice.server.configuration.amqp

import dvoraka.avservice.server.amqp.AmqpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [AmqpClientConfig.class])
@ActiveProfiles(["amqp-client"])
class AmqpClientConfigISpec extends Specification {

    @Autowired
    AmqpClient amqpClient


    def "test"() {
        expect:
            true
    }
}
