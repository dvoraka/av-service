package dvoraka.avservice.server.configuration

import dvoraka.avservice.server.ServerComponentBridge
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [JmsToAmqpConfig.class])
@ActiveProfiles(["jms2amqp", "no-db"])
class JmsToAmqpConfigISpec extends Specification {

    @Autowired
    ServerComponentBridge bridge


    def "test"() {
        expect:
            true
    }
}
