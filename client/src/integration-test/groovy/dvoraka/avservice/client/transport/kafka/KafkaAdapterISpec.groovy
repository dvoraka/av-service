package dvoraka.avservice.client.transport.kafka

import dvoraka.avservice.client.configuration.ClientConfig
import dvoraka.avservice.client.transport.AvNetworkComponent
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.listener.AvMessageListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

@ContextConfiguration(classes = [ClientConfig.class])
@ActiveProfiles(["client", "checker", "kafka", "file-client", "no-db"])
@Ignore
class KafkaAdapterISpec extends Specification {

    @Autowired
    AvNetworkComponent avNetworkComponent


    def setup() {
        avNetworkComponent.addMessageListener(new AvMessageListener() {
            @Override
            void onMessage(AvMessage message) {
                println 'M:' + message
            }
        })
    }

    def "consume"() {
        expect:
            sleep(6_000)
    }
}
