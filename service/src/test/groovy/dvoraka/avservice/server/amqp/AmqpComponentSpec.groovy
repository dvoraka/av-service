package dvoraka.avservice.server.amqp

import dvoraka.avservice.common.AVMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AVMessage
import dvoraka.avservice.common.data.DefaultAVMessage
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Ignore
import spock.lang.Specification

/**
 * AMQP component test.
 */
class AmqpComponentSpec extends Specification {

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
        given:
        AVMessageListener listener1 = getAVMessageListener()
        AVMessageListener listener2 = getAVMessageListener()

        when:
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

    def "send null message"() {
        when:
        component.sendMessage(null)

        then:
        thrown(IllegalArgumentException)
    }

    def "send normal message"() {
        given:
        RabbitTemplate rabbitTemplate = Mock()
        ReflectionTestUtils.setField(component, null, rabbitTemplate, RabbitTemplate.class)

        AVMessage message = Utils.genNormalMessage()

        when:
        component.sendMessage(message)

        then:
        1 * rabbitTemplate.send(_, _, _)
    }

    def "send broken message"() {
        given:
        RabbitTemplate rabbitTemplate = Mock()
        ReflectionTestUtils.setField(component, null, rabbitTemplate, RabbitTemplate.class)

        AVMessage message = new DefaultAVMessage.Builder(null)
                .build()

        when:
        component.sendMessage(message)

        then:
        1 * rabbitTemplate.send(_, _, _)
    }

    AVMessageListener getAVMessageListener() {
        return new AVMessageListener() {
            @Override
            void onAVMessage(AVMessage message) {

            }
        }
    }
}
