package dvoraka.avservice.rest.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.rest.Application
import dvoraka.avservice.rest.RestClient
import dvoraka.avservice.rest.configuration.RestClientConfig
import dvoraka.avservice.rest.controller.CheckController
import dvoraka.avservice.rest.controller.FileController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @Autowired
    TestRestTemplate simpleTemplate

    TestRestTemplate restTemplate

    String checkPath = CheckController.MAPPING + '/'
    String savePath = FileController.MAPPING + '/save'
    String loadPath = FileController.MAPPING + '/load'


    def setup() {
        restTemplate = simpleTemplate.withBasicAuth('test', 'test')
    }

    def "get info"() {
        when:
            ResponseEntity<String> response = restTemplate
                    .getForEntity('/', String.class)
            String message = response.getBody()

        then:
            response.getStatusCode() == HttpStatus.OK
            message != null
            message == 'AV service'
    }

    def "get testing message"() {
        when:
            ResponseEntity<AvMessage> response = restTemplate
                    .getForEntity('/gen-msg', DefaultAvMessage.class)
            AvMessage message = response.getBody()

        then:
            response.getStatusCode() == HttpStatus.OK
            message != null
            message.getServiceId() == 'REST'
    }

    def "check normal message"() {
        setup:
            AvMessage message = Utils.genMessage()
            String id = message.getId()

            client.postMessage(message, checkPath)
            sleep(2000)

            MessageStatus status = client.getMessageStatus("/msg-status/" + id)
            AvMessage response = client.getMessage("/get-response/" + id)

        expect:
            status == MessageStatus.PROCESSED
            response.type == MessageType.RESPONSE
            response.getVirusInfo() == Utils.OK_VIRUS_INFO
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
            response.type == MessageType.RESPONSE
            response.getVirusInfo() != Utils.OK_VIRUS_INFO
    }

    //
    // File operations
    //
    def "save message with unauthorized random username"() {
        given:
            AvMessage message = Utils.genFileMessage()

        when:
            ResponseEntity<Void> response = simpleTemplate
                    .postForEntity(savePath, message, Void.class)

        then:
            response.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def "save message with authorized random username"() {
        given:
            AvMessage message = Utils.genFileMessage()

        when:
            ResponseEntity<Void> response = restTemplate
                    .postForEntity(savePath, message, Void.class)

        then:
            response.getStatusCode() == HttpStatus.FORBIDDEN
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
            Arrays.equals(message.getData(), loaded.getData())
    }

    // validations

    def "normal message validation"() {
        when:
            client.postMessage(Utils.genMessage(), checkPath)

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
