package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.configuration.AppConfig
import dvoraka.avservice.data.AVMessage
import dvoraka.avservice.data.AVMessageType
import dvoraka.avservice.data.MessageStatus
import dvoraka.avservice.rest.RestClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * REST testing.
 */
@ContextConfiguration(classes = [AppConfig])
class RestServiceISpec extends Specification {

    @Autowired
    RestClient client;


    def "get testing message"() {
        setup:
        AVMessage message = client.getMessage("/gen-msg")

        expect:
        message != null
        message.getServiceId().equals("testing-service")
    }

    def "send normal message"() {
        setup:
        AVMessage message = Utils.genNormalMessage()

        expect:
        client.postMessage(message, "/msg-check")
    }

    def "check normal message"() {
        setup:
        AVMessage message = Utils.genNormalMessage()
        String id = message.getId()

        client.postMessage(message, "/msg-check")
        sleep(2000)

        MessageStatus status = client.getMessageStatus("/msg-status/" + id)
        AVMessage response = client.getMessage("/get-response/" + id)

        expect:
        status == MessageStatus.PROCESSED
        response.type == AVMessageType.RESPONSE
        response.getVirusInfo().equals("false")
    }

    def "send infected message"() {
        setup:
        AVMessage message = Utils.genInfectedMessage()

        expect:
        client.postMessage(message, "/msg-check")
    }

    def "check infected message"() {
        setup:
        AVMessage message = Utils.genInfectedMessage()
        String id = message.getId()

        client.postMessage(message, "/msg-check")
        sleep(2000)

        MessageStatus status = client.getMessageStatus("/msg-status/" + id)
        AVMessage response = client.getMessage("/get-response/" + id)

        expect:
        status == MessageStatus.PROCESSED
        response.type == AVMessageType.RESPONSE
        response.getVirusInfo().equals("true")
    }
}
