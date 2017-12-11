package dvoraka.avservice.client.service.response

import dvoraka.avservice.client.AvNetworkComponent
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.util.Utils
import dvoraka.avservice.db.service.MessageInfoService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

/**
 * Client spec.
 */
class DefaultResponseClientSpec extends Specification {

    @Subject
    DefaultResponseClient client

    AvNetworkComponent serverComponent
    MessageInfoService messageInfoService

    @Shared
    String serviceId = 'testID'


    def setup() {
        serverComponent = Mock()
        serverComponent.getServiceId() >> serviceId
        messageInfoService = Mock()

        client = new DefaultResponseClient(serverComponent, messageInfoService)
    }

    def "start and stop"() {
        expect:
            !client.isStarted()

        when:
            client.start()

        then:
            client.isStarted()
            1 * serverComponent.addMessageListener(_)

        when: "start again"
            client.start()

        then:
            0 * serverComponent.addMessageListener(_)

        when:
            client.stop()

        then:
            !client.isStarted()
            1 * serverComponent.removeMessageListener(_)

        when: "stop again"
            client.stop()

        then:
            0 * serverComponent.removeMessageListener(_)
    }

    def "get response before start"() {
        when:
            client.getResponse('response')

        then:
            thrown(IllegalStateException)
    }

    def "get random response"() {
        when:
            client.start()

        then:
            client.getResponse('response') == null
    }

    def "test cache"() {
        given:
            AvMessage response = Utils.genFileMessage()

        when:
            client.start()
            client.onMessage(response)

        then:
            client.getResponse(response.getCorrelationId()) == response
    }
}
