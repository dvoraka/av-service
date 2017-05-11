package dvoraka.avservice.client.service

import dvoraka.avservice.client.ServerComponent
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import spock.lang.Specification
import spock.lang.Subject

/**
 * Client spec.
 */
class DefaultAvServiceClientSpec extends Specification {

    @Subject
    DefaultAvServiceClient client

    ServerComponent serverComponent


    def setup() {
        serverComponent = Mock()
        client = new DefaultAvServiceClient(serverComponent)
    }

    def "check message with correct type"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            client.checkMessage(message)

        then:
            1 * serverComponent.sendAvMessage(message)
    }

    def "check message with incorrect type"() {
        given:
            AvMessage message = Utils.genFileMessage()

        when:
            client.checkMessage(message)

        then:
            0 * serverComponent.sendAvMessage(message)
            thrown(IllegalArgumentException)
    }
}
