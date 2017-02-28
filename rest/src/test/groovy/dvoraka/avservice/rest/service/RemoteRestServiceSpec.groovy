package dvoraka.avservice.rest.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.server.ServerComponent
import spock.lang.Specification
import spock.lang.Subject

/**
 * Remote REST service spec.
 */
class RemoteRestServiceSpec extends Specification {

    @Subject
    RemoteRestService service

    ServerComponent serverComponent


    def setup() {
        serverComponent = Mock()
        service = new RemoteRestService(serverComponent)
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
            1 * serverComponent.sendAvMessage(message)
            service.messageStatus(message.getId()) == MessageStatus.PROCESSING
    }

    def "save message"() {
        given:
            AvMessage message = Utils.genSaveMessage()

        when:
            service.saveMessage(message)

        then:
            1 * serverComponent.sendAvMessage(message)
            service.messageStatus(message.getId()) == MessageStatus.PROCESSING
    }

    def "load message"() {
        given:
            AvMessage message = Utils.genLoadMessage()

        when:
            service.loadMessage(message)

        then:
            1 * serverComponent.sendAvMessage(message)
            service.messageStatus(message.getId()) == MessageStatus.PROCESSING
    }

    def "update message"() {
        given:
            AvMessage message = Utils.genUpdateMessage()

        when:
            service.updateMessage(message)

        then:
            1 * serverComponent.sendAvMessage(message)
            service.messageStatus(message.getId()) == MessageStatus.PROCESSING
    }

    def "delete message"() {
        given:
            AvMessage message = Utils.genDeleteMessage()

        when:
            service.deleteMessage(message)

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
            AvMessage response = message.createResponse("")
            service.onAvMessage(response)

        then:
            service.messageStatus(message.getId()) != MessageStatus.PROCESSING
            service.messageStatus(message.getId()) == MessageStatus.PROCESSED
    }
}
