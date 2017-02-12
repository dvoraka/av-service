package dvoraka.avservice.rest.service

import com.fasterxml.jackson.databind.ObjectMapper
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.rest.Application
import dvoraka.avservice.rest.configuration.RestClientConfig
import dvoraka.avservice.rest.controller.CheckController
import dvoraka.avservice.rest.controller.FileController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Ignore
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
    TestRestTemplate basicRestTemplate
    @Autowired
    ObjectMapper objectMapper

    TestRestTemplate restTemplate

    String testUsername = 'test'
    String testPassword = 'test'

    String checkPath = CheckController.MAPPING + '/'
    String filePath = FileController.MAPPING + '/'


    def setup() {
        restTemplate = basicRestTemplate.withBasicAuth(testUsername, testPassword)
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
        given:
            AvMessage normalMessage = Utils.genMessage()
            String id = normalMessage.getId()

        when:
            restTemplate.postForEntity(checkPath, normalMessage, DefaultAvMessage.class)
            sleep(2000)

            ResponseEntity<MessageStatus> statusResponseEntity = restTemplate
                    .getForEntity('/msg-status/' + id, MessageStatus.class)
            MessageStatus status = statusResponseEntity.getBody()

            ResponseEntity<AvMessage> messageResponseEntity = restTemplate
                    .getForEntity('/get-response/' + id, DefaultAvMessage.class)
            AvMessage message = messageResponseEntity.getBody()

        then:
            statusResponseEntity.getStatusCode() == HttpStatus.OK
            status == MessageStatus.PROCESSED

            messageResponseEntity.getStatusCode() == HttpStatus.OK
            message.type == MessageType.RESPONSE
            message.getVirusInfo() == Utils.OK_VIRUS_INFO
    }

    def "check infected message"() {
        given:
            AvMessage infectedMessage = Utils.genInfectedMessage()
            String id = infectedMessage.getId()

        when:
            restTemplate.postForEntity(checkPath, infectedMessage, DefaultAvMessage.class)
            sleep(2000)

            ResponseEntity<MessageStatus> statusResponseEntity = restTemplate
                    .getForEntity('/msg-status/' + id, MessageStatus.class)
            MessageStatus status = statusResponseEntity.getBody()

            ResponseEntity<AvMessage> messageResponseEntity = restTemplate
                    .getForEntity('/get-response/' + id, DefaultAvMessage.class)
            AvMessage message = messageResponseEntity.getBody()

        then:
            statusResponseEntity.getStatusCode() == HttpStatus.OK
            status == MessageStatus.PROCESSED

            messageResponseEntity.getStatusCode() == HttpStatus.OK
            message.type == MessageType.RESPONSE
            message.getVirusInfo() != Utils.OK_VIRUS_INFO
    }

    //
    // File operations
    //
    def "save message with unauthorized random username"() {
        given:
            AvMessage message = Utils.genFileMessage()

        when:
            ResponseEntity<Void> responseEntity = basicRestTemplate
                    .postForEntity(filePath, message, Void.class)

        then:
            responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED
    }

    def "save message with authorized random username"() {
        given:
            AvMessage message = Utils.genFileMessage()

        when:
            ResponseEntity<Void> responseEntity = restTemplate
                    .postForEntity(filePath, message, Void.class)

        then:
            responseEntity.getStatusCode() == HttpStatus.FORBIDDEN
    }

    def "save message with test username"() {
        given:
            AvMessage message = Utils.genFileMessage(testUsername)

        when:
            ResponseEntity<Void> responseEntity = restTemplate
                    .postForEntity(filePath, message, Void.class)

        then:
            responseEntity.getStatusCode() == HttpStatus.ACCEPTED
    }

    def "save and load message"() {
        given:
            AvMessage message = Utils.genFileMessage(testUsername)
            String loadUrl = filePath + message.getFilename()

        when:
            ResponseEntity<Void> responseEntity = restTemplate
                    .postForEntity(filePath, message, Void.class)

        then:
            responseEntity.getStatusCode() == HttpStatus.ACCEPTED
            sleep(1000)

        when:
            ResponseEntity<AvMessage> messageResponseEntity = restTemplate
                    .getForEntity(loadUrl, DefaultAvMessage.class)
            AvMessage loaded = messageResponseEntity.getBody()

        then:
            messageResponseEntity.getStatusCode() == HttpStatus.OK
            loaded != null
            loaded.getFilename() == message.getFilename()
            loaded.getOwner() == message.getOwner()
            Arrays.equals(message.getData(), loaded.getData())
    }

    def "save and update message"() {
        given:
            AvMessage message = Utils.genFileMessage(testUsername)
            AvMessage updateMessage = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .filename(message.getFilename())
                    .owner(message.getOwner())
                    .data(new byte[2])
                    .type(MessageType.FILE_UPDATE)
                    .build()

            String loadUrl = filePath + message.getFilename()

        when: "save"
            ResponseEntity<Void> responseEntity = restTemplate
                    .postForEntity(filePath, message, Void.class)

        then:
            responseEntity.getStatusCode() == HttpStatus.ACCEPTED
            sleep(1000)

        when: "load"
            ResponseEntity<AvMessage> messageResponseEntity = restTemplate
                    .getForEntity(loadUrl, DefaultAvMessage.class)
            AvMessage loaded = messageResponseEntity.getBody()

        then:
            messageResponseEntity.getStatusCode() == HttpStatus.OK
            loaded != null
            loaded.getFilename() == message.getFilename()
            loaded.getOwner() == message.getOwner()
            loaded.getType() == MessageType.FILE_RESPONSE
            Arrays.equals(message.getData(), loaded.getData())

        when: "update and load"
            restTemplate.put(filePath + message.getFilename(), updateMessage)
            sleep(1000)

            messageResponseEntity = restTemplate
                    .getForEntity(loadUrl, DefaultAvMessage.class)
            loaded = messageResponseEntity.getBody()

        then:
            messageResponseEntity.getStatusCode() == HttpStatus.OK
            loaded != null
            loaded.getFilename() == message.getFilename()
            loaded.getOwner() == message.getOwner()
            loaded.getType() == MessageType.FILE_RESPONSE
            Arrays.equals(updateMessage.getData(), loaded.getData())
    }

    def "save and delete message"() {
        given:
            AvMessage message = Utils.genFileMessage(testUsername)
            String loadUrl = filePath + message.getFilename()

        when: "save"
            ResponseEntity<Void> responseEntity = restTemplate
                    .postForEntity(filePath, message, Void.class)

        then:
            responseEntity.getStatusCode() == HttpStatus.ACCEPTED
            sleep(1000)

        when: "load"
            ResponseEntity<AvMessage> messageResponseEntity = restTemplate
                    .getForEntity(loadUrl, DefaultAvMessage.class)
            AvMessage loaded = messageResponseEntity.getBody()

        then:
            messageResponseEntity.getStatusCode() == HttpStatus.OK
            loaded != null
            loaded.getFilename() == message.getFilename()
            loaded.getOwner() == message.getOwner()
            loaded.getType() == MessageType.FILE_RESPONSE
            Arrays.equals(message.getData(), loaded.getData())

        when: "delete and load"
            restTemplate.delete(filePath + message.getFilename())
            sleep(1000)

            messageResponseEntity = restTemplate
                    .getForEntity(loadUrl, DefaultAvMessage.class)
            loaded = messageResponseEntity.getBody()

        then:
            messageResponseEntity.getStatusCode() == HttpStatus.OK
            loaded != null
            loaded.getFilename() == message.getFilename()
            loaded.getOwner() == message.getOwner()
            loaded.getType() == MessageType.FILE_NOT_FOUND
    }

    //
    // Validations
    //
    @Ignore('WIP')
    def "send broken message"() {
        given:
            AvMessage message = Utils.genMessage()
            ReflectionTestUtils.setField(message, "id", null)

            String json = objectMapper.writeValueAsString(message)

            HttpHeaders headers = new HttpHeaders()
            headers.setContentType(MediaType.APPLICATION_JSON)

            HttpEntity<String> entity = new HttpEntity<>(json, headers)

        when:
            ResponseEntity<String> messageResponseEntity = restTemplate
                    .postForEntity(checkPath, entity, String.class)

        then:
            println messageResponseEntity
    }
}
