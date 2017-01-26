package dvoraka.avservice.rest

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.server.ServerComponent
import spock.lang.Specification
import spock.lang.Subject

/**
 * Remote REST strategy spec.
 */
class RemoteRestStrategySpec extends Specification {

    @Subject
    RemoteRestStrategy strategy

    ServerComponent serverComponent


    def setup() {
        serverComponent = Mock()
        strategy = new RemoteRestStrategy(serverComponent)
    }

    def cleanup() {
        strategy.stop()
    }

    def "start"() {
        when:
            strategy.start()

        then:
            notThrown(Exception)
            strategy.isStarted()
    }

    def "start twice"() {
        when:
            strategy.start()

        then:
            1 * serverComponent.addAvMessageListener(_)
            strategy.isStarted()

        when: "start again"
            strategy.start()

        then:
            0 * serverComponent.addAvMessageListener(_)
            strategy.isStarted()
    }

    def "start and stop"() {
        when:
            strategy.start()

        then:
            notThrown(Exception)
            strategy.isStarted()

        when:
            strategy.stop()

        then:
            notThrown(Exception)
            !strategy.isStarted()
    }

    def "get message status without sent message"() {
        expect:
            strategy.messageStatus('AAA') == MessageStatus.UNKNOWN
    }

    def "get message service ID"() {
        expect:
            strategy.messageServiceId('AAA') == null
    }

    def "message check"() {
        given:
            AvMessage message = Utils.genNormalMessage()

        when:
            strategy.checkMessage(message)

        then:
            1 * serverComponent.sendAvMessage(message)
            strategy.messageStatus(message.getId()) == MessageStatus.PROCESSING
    }

    def "get message from cache without sending"() {
        when:
            strategy.start()

        then:
            strategy.getResponse('AAA') == null
    }

    def "check message, send response and check status"() {
        given:
            AvMessage message = Utils.genNormalMessage()
            strategy.start()

        when:
            strategy.checkMessage(message)

        then:
            strategy.messageStatus(message.getId()) == MessageStatus.PROCESSING

        when:
            AvMessage response = message.createResponse("")
            strategy.onAvMessage(response)

        then:
            strategy.messageStatus(message.getId()) != MessageStatus.PROCESSING
            strategy.messageStatus(message.getId()) == MessageStatus.PROCESSED
    }
}
