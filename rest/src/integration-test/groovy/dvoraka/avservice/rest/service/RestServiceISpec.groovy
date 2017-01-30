package dvoraka.avservice.rest.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageType
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.rest.Application
import dvoraka.avservice.rest.RestClient
import dvoraka.avservice.rest.configuration.RestClientConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

/**
 * REST testing.
 */
@SpringBootTest(
        classes = [
                Application.class,
                RestClientConfig.class
        ],
        properties = [
                'spring.profiles.active=rest-client,core,rest,rest-local,storage,db',
                'server.contextPath=/av-service',
                'port=8080'
        ],
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
class RestServiceISpec extends Specification {

    @Autowired
    RestClient client

    String checkPath = '/msg-check'
    String savePath = '/file/save'
    String loadPath = '/file/load'


    def "get testing message"() {
        setup:
            AvMessage message = client.getMessage("/gen-msg")

        expect:
            message != null
            message.getServiceId() == 'REST'
    }

    def "send normal message"() {
        given:
            AvMessage message = Utils.genNormalMessage()

        when:
            client.postMessage(message, checkPath)

        then:
            notThrown(Exception)
    }

    def "save message with random username"() {
        given:
            AvMessage message = Utils.genFileMessage()

        when:
            client.postMessage(message, savePath)

        then:
            thrown(Exception)
    }

    def "save message with test username"() {
        given:
            AvMessage message = Utils.genFileMessage('test')

        when:
            client.postMessage(message, savePath)

        then:
            notThrown(Exception)
    }

    def "save and load message"() {
        given:
            AvMessage message = Utils.genFileMessage('test')
            String loadUrl = loadPath + '/' + message.getFilename()

        when:
            client.postMessage(message, savePath)

        then:
            notThrown(Exception)

        when:
            AvMessage loaded = client.getMessage(loadUrl)

        then:
            loaded.getFilename() == message.getFilename()
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

    // validations

    def "normal message validation"() {
        when:
            client.postMessage(Utils.genNormalMessage(), checkPath)

        then:
            notThrown(Exception)
    }

    def "infected message validation"() {
        when:
            client.postMessage(Utils.genInfectedMessage(), checkPath)

        then:
            notThrown(Exception)
    }
}
