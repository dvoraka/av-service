package dvoraka.avservice.client.configuration

import dvoraka.avservice.client.ServerComponent
import dvoraka.avservice.common.Utils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [KafkaFileClientConfig.class])
@DirtiesContext
@ActiveProfiles(['kafka'])
class KafkaFileClientConfigISpec extends Specification {

    @Autowired
    ServerComponent serverComponent


    def "test"() {
        expect:
            true
    }

    def "send message"() {
        expect:
            serverComponent.sendAvMessage(Utils.genMessage())
    }
}
