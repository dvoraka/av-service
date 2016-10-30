package dvoraka.avservice.server.configuration.amqp

import dvoraka.avservice.server.amqp.AmqpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(["amqp", "amqp-client"])
class AmqpClientConfigISpec extends Specification {

    @Autowired
    AmqpClient amqpClient


    def "test"() {
        expect:
            true
    }
}
