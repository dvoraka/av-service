package dvoraka.avservice.rest

import dvoraka.avservice.MessageProcessor
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.MessageStatus
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

/**
 * Direct REST strategy test.
 */
class DirectRestStrategySpec extends Specification {

    String testId = 'TEST-ID'

    DirectRestStrategy strategy
    PollingConditions conditions


    def setup() {
        strategy = new DirectRestStrategy()
        conditions = new PollingConditions(timeout: 2)
    }

    def cleanup() {
        strategy.stop()
    }

    def "unknown message status"() {
        setup:
        MessageProcessor processor = Stub()

        processor.messageStatus(_) >> MessageStatus.UNKNOWN

        strategy.setRestMessageProcessor(processor)
        strategy.start()

        expect:
        strategy.messageStatus("NEWID").equals(MessageStatus.UNKNOWN)
        strategy.messageStatus("NEWID", "SERVICEID").equals(MessageStatus.UNKNOWN)
    }

    def "message check"() {
        setup:
        MessageProcessor processor = Mock()
        strategy.setRestMessageProcessor(processor)
        strategy.start()

        when:
        strategy.messageCheck(Utils.genNormalMessage())

        then:
        1 * processor.sendMessage(_)
    }

    def "get null response"() {
        setup:
        MessageProcessor processor = Mock()
        strategy.setRestMessageProcessor(processor)
        strategy.start()

        expect:
        strategy.getResponse("NEWID") == null
    }

    def "get real response"() {
        setup:
        AvMessage request = new DefaultAvMessage.Builder(testId).build()
        AvMessage response = request.createResponse(false)

        MessageProcessor processor = Stub()

        processor.hasProcessedMessage() >>> [true, false]
        processor.getProcessedMessage() >> response

        strategy.setRestMessageProcessor(processor)
        strategy.start()

        expect:
        conditions.eventually {
            strategy.getResponse(testId).equals(response)
        }
    }
}
