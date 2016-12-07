package dvoraka.avservice.rest.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.rest.RestStrategy
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification

/**
 * DefaultRestService spec.
 */
class DefaultRestServiceSpec extends Specification {

    DefaultRestService service
    RestStrategy strategy


    def setup() {
        strategy = Stub()
        service = new DefaultRestService(strategy)
    }

    def "message status with processed status"() {
        given:
            String id = "TEST"
            MessageStatus expStatus = MessageStatus.PROCESSED
            strategy.messageStatus(id, _) >> expStatus

        when:
            MessageStatus status = service.messageStatus(id)

        then:
            status == expStatus
    }

    def "message status with unknown status"() {
        given:
            String id = "TEST"
            MessageStatus expStatus = MessageStatus.UNKNOWN
            strategy.messageStatus(id, _) >> expStatus

        when:
            MessageStatus status = service.messageStatus(id)

        then:
            status == expStatus
    }

    def "message status with waiting status"() {
        given:
            AvMessage message = Utils.genNormalMessage()
            MessageStatus expStatus = MessageStatus.UNKNOWN
            strategy.messageStatus(message.getId(), _) >> expStatus

        when:
            service.messageCheck(message)
            MessageStatus status = service.messageStatus(message.getId())

        then:
            status == MessageStatus.WAITING
    }

    def "message service id"() {
        given:
            String id = "TEST"
            String sId = "service1"
            strategy.messageServiceId(id) >> sId

        when:
            String result = service.messageServiceId(id)

        then:
            result == sId
    }

    def "get response"() {
        given:
            AvMessage message = Utils.genNormalMessage()
            strategy.getResponse(message.getId()) >> message

        when:
            AvMessage response = service.getResponse(message.getId())

        then:
            response == message
    }

    def "stop call"() {
        given:
            strategy = Mock()
            ReflectionTestUtils.setField(service, null, strategy, RestStrategy.class)

        when:
            service.stop()

        then:
            1 * strategy.stop()
    }
}
