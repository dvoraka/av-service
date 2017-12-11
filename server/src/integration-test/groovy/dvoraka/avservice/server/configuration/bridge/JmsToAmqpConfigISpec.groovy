package dvoraka.avservice.server.configuration.bridge

import dvoraka.avservice.server.AvNetworkComponentBridge
import dvoraka.avservice.server.configuration.BridgeConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [BridgeConfig.class])
@DirtiesContext
@ActiveProfiles(['bridge', 'jms', 'to-amqp', 'no-db'])
class JmsToAmqpConfigISpec extends Specification {

    @Autowired
    AvNetworkComponentBridge bridge


    def "test"() {
        expect:
            true
    }
}
