package dvoraka.avservice.rest.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.server.ServerComponent
import dvoraka.avservice.server.client.service.AvServiceClient
import spock.lang.Specification
import spock.lang.Subject

/**
 * Remote REST service spec.
 */
class RemoteRestServiceSpec extends Specification {

    @Subject
    RemoteRestService service

    ServerComponent serverComponent
    AvServiceClient avServiceClient


    def setup() {
        serverComponent = Mock()
        avServiceClient = Mock()
        service = new RemoteRestService(serverComponent, avServiceClient)
    }

    def cleanup() {
        service.stop()
    }

    def "start"() {
        when:
            service.start()

        then:
            notThrown(Exception)
            service.isStarted()
    }

    def "start twice"() {
        when:
            service.start()

        then:
            1 * serverComponent.addAvMessageListener(_)
            service.isStarted()

        when: "start again"
            service.start()

        then:
            0 * serverComponent.addAvMessageListener(_)
            service.isStarted()
    }

    def "start and stop"() {
        when:
            service.start()

        then:
            notThrown(Exception)
            service.isStarted()

        when:
            service.stop()

        then:
            notThrown(Exception)
            !service.isStarted()
    }

    def "get message status without sent message"() {
        expect:
            service.messageStatus('AAA') == MessageStatus.UNKNOWN
    }

    def "check message"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            service.checkMessage(message)

        then:
            1 * avServiceClient.checkMessage(message)
            service.messageStatus(message.getId()) == MessageStatus.PROCESSING
    }

    def "save message"() {
        given:
            AvMessage message = Utils.genSaveMessage()

        when:
            service.saveFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
            service.messageStatus(message.getId()) == MessageStatus.PROCESSING
    }

    def "load message"() {
        given:
            AvMessage message = Utils.genLoadMessage()

        when:
            service.loadFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
            service.messageStatus(message.getId()) == MessageStatus.PROCESSING
    }

    def "update message"() {
        given:
            AvMessage message = Utils.genUpdateMessage()

        when:
            service.updateFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
            service.messageStatus(message.getId()) == MessageStatus.PROCESSING
    }

    def "delete message"() {
        given:
            AvMessage message = Utils.genDeleteMessage()

        when:
            service.deleteFile(message)

        then:
            1 * serverComponent.sendAvMessage(message)
            service.messageStatus(message.getId()) == MessageStatus.PROCESSING
    }

    def "get message from cache without sending"() {
        when:
            service.start()

        then:
            service.getResponse('AAA') == null
    }

    def "check message, send response and check status"() {
        given:
            AvMessage message = Utils.genMessage()
            service.start()

        when:
            service.checkMessage(message)

        then:
            service.messageStatus(message.getId()) == MessageStatus.PROCESSING

        when:
            AvMessage response = message.createCheckResponse("")
            service.onAvMessage(response)

        then:
            service.messageStatus(message.getId()) != MessageStatus.PROCESSING
            service.messageStatus(message.getId()) == MessageStatus.PROCESSED
    }
}
