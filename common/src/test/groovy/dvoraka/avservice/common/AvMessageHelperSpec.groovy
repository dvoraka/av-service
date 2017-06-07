package dvoraka.avservice.common

import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.helper.AvMessageHelper
import spock.lang.Specification
import spock.lang.Subject

/**
 * AvMessageHelper spec.
 */
class AvMessageHelperSpec extends Specification {

    @Subject
    AvMessageHelper helper


    def setup() {
        helper = Spy()
    }

    def "notify listeners"() {
        given:
            Iterable<AvMessageListener> listeners = new ArrayList<>()
            AvMessageListener listener1 = Mock()
            AvMessageListener listener2 = Mock()
            listeners.add(listener1)
            listeners.add(listener2)

            AvMessage message = Utils.genMessage()

        when:
            helper.notifyListeners(listeners, message)

        then:
            1 * listener1.onAvMessage(message)
            1 * listener2.onAvMessage(message)
    }

    def "prepare response"() {
        given:
            AvMessage message = Mock()
            String info = 'info'

        when:
            helper.prepareResponse(message, info)

        then:
            1 * message.createCheckResponse(info)
    }

    def "prepare error response"() {
        given:
            AvMessage message = Mock()
            String error = 'error'

        when:
            helper.prepareErrorResponse(message, error)

        then:
            1 * message.createErrorResponse(error)
    }
}
