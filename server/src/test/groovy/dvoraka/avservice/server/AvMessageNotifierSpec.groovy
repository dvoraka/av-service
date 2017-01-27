package dvoraka.avservice.server

import dvoraka.avservice.common.AvMessageHelper
import dvoraka.avservice.common.AvMessageListener
import spock.lang.Specification
import spock.lang.Subject

/**
 * AV message notifier spec.
 */
class AvMessageNotifierSpec extends Specification {

    @Subject
    AvMessageHelper notifier


    def setup() {
        notifier = Spy()
    }

    def "test notifyListeners"() {
        given:
            int listenerCount = 3
            List<AvMessageListener> listeners = new ArrayList<>()
            AvMessageListener listener = Mock()

            listenerCount.times {
                listeners.add(listener)
            }

        when:
            notifier.notifyListeners(listeners, null)

        then:
            listenerCount * listener.onAvMessage(null)
    }
}
