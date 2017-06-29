package dvoraka.avservice.client.configuration

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Configuration test.
 */
@ContextConfiguration(classes = [KafkaClientConfig.class])
@DirtiesContext
@ActiveProfiles(['kafka'])
class KafkaClientConfigISpec extends Specification {

//    @Autowired
//    ServerComponent serverComponent

    @Autowired
    KafkaTemplate<String, AvMessage> kafkaTemplate


    def "test"() {
        expect:
            true
    }

    def "send message"() {
        expect:
            kafkaTemplate.send('test.t', Utils.genMessage())
    }
}
