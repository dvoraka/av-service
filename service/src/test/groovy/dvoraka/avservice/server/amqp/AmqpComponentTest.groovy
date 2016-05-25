package dvoraka.avservice.server.amqp

import dvoraka.avservice.common.AVMessageListener
import dvoraka.avservice.common.data.AVMessage
import spock.lang.Specification

/**
 * AMQP component test.
 */
class AmqpComponentTest extends Specification {

    AmqpComponent component


    def setup() {
        component = new AmqpComponent("NONE")
    }

    def "add listeners"() {
        when:
        component.addAVMessageListener(getAVMessageListener())
        component.addAVMessageListener(getAVMessageListener())

        then:
        component.listenersCount() == 2
    }

    def "remove listeners"() {
        when:
        AVMessageListener listener1 = getAVMessageListener()
        AVMessageListener listener2 = getAVMessageListener()

        component.addAVMessageListener(listener1)
        component.addAVMessageListener(listener2)

        then:
        component.listenersCount() == 2

        when:
        component.removeAVMessageListener(listener1)
        component.removeAVMessageListener(listener2)

        then:
        component.listenersCount() == 0
    }

    AVMessageListener getAVMessageListener() {
        return new AVMessageListener() {
            @Override
            void onAVMessage(AVMessage message) {

            }
        }
    }
}
