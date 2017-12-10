package dvoraka.avservice.rest.service

import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.common.util.Utils
import dvoraka.avservice.core.MessageProcessor
import spock.lang.Specification
import spock.lang.Subject
import spock.util.concurrent.PollingConditions

/**
 * Local REST service spec.
 */
class LocalRestServiceSpec extends Specification {

    @Subject
    LocalRestService strategy

    MessageProcessor compositeProcessor
    MessageProcessor fileProcessor
    String testId = 'TEST-ID'

    PollingConditions conditions


    def setup() {
        compositeProcessor = Mock()
        fileProcessor = Mock()
        strategy = new LocalRestService(compositeProcessor)
        conditions = new PollingConditions(timeout: 2)
    }

    def cleanup() {
        strategy.stop()
    }

    def "unknown message status"() {
        setup:
            compositeProcessor.messageStatus(_) >> MessageStatus.UNKNOWN
            strategy.start()

        expect:
            strategy.messageStatus('NEWID') == MessageStatus.UNKNOWN
    }

    def "message check"() {
        setup:
            strategy.start()

        when:
            strategy.checkMessage(Utils.genMessage())

        then:
            1 * compositeProcessor.sendMessage(_)
    }

    def "get null response"() {
        setup:
            strategy.start()

        expect:
            strategy.getResponse('NEWID') == null
    }

    def "get real response"() {
        given:
            AvMessage request = new DefaultAvMessage.Builder(testId).build()
            AvMessage response = request.createCheckResponse(Utils.OK_VIRUS_INFO)

        when:
            strategy.start()
            strategy.onMessage(response)

        then:
            conditions.eventually {
                strategy.getResponse(testId) == response
            }
    }
}
