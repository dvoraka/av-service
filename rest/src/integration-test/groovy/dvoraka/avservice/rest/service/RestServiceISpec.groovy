package dvoraka.avservice.rest.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageType
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.rest.RestClient
import dvoraka.avservice.rest.configuration.RestClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * REST testing.
 */
@ContextConfiguration(classes = [RestClientConfig.class])
@ActiveProfiles("rest-client")
class RestServiceISpec extends Specification {

    @Autowired
    RestClient client

    String checkPath = "/msg-check"


    def "get testing message"() {
        setup:
            AvMessage message = client.getMessage("/gen-msg")

        expect:
            message != null
            message.getServiceId() == "testing-service"
    }

    def "send normal message"() {
        given:
            AvMessage message = Utils.genNormalMessage()

        when:
            client.postMessage(message, checkPath)

        then:
            notThrown(Exception)
    }

    def "check normal message"() {
        setup:
            AvMessage message = Utils.genNormalMessage()
            String id = message.getId()

            client.postMessage(message, checkPath)
            sleep(2000)

            MessageStatus status = client.getMessageStatus("/msg-status/" + id)
            AvMessage response = client.getMessage("/get-response/" + id)

        expect:
            status == MessageStatus.PROCESSED
            response.type == AvMessageType.RESPONSE
            response.getVirusInfo() == Utils.OK_VIRUS_INFO
    }

    def "send infected message"() {
        given:
            AvMessage message = Utils.genInfectedMessage()

        when:
            client.postMessage(message, checkPath)

        then:
            notThrown(Exception)
    }

    def "check infected message"() {
        setup:
            AvMessage message = Utils.genInfectedMessage()
            String id = message.getId()

            client.postMessage(message, checkPath)
            sleep(2000)

            MessageStatus status = client.getMessageStatus("/msg-status/" + id)
            AvMessage response = client.getMessage("/get-response/" + id)

        expect:
            status == MessageStatus.PROCESSED
            response.type == AvMessageType.RESPONSE
            response.getVirusInfo() != Utils.OK_VIRUS_INFO
    }

    @Ignore
    def "check message validation"() {
        setup:
            AvMessage message = Utils.genNormalMessage()

            AvMessage response = client.postMessage(message, checkPath)

        expect:
            println(response)
    }
}
