package dvoraka.avservice.server.amqp

import dvoraka.avservice.common.AvMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.test.util.ReflectionTestUtils
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
        AvMessageListener listener1 = getAVMessageListener()
        AvMessageListener listener2 = getAVMessageListener()

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

        AvMessage message = Utils.genNormalMessage()

        when:
        component.sendMessage(message)

        then:
        1 * rabbitTemplate.send(_, _, _)
    }

    def "send broken message"() {
        given:
        RabbitTemplate rabbitTemplate = Mock()
        ReflectionTestUtils.setField(component, null, rabbitTemplate, RabbitTemplate.class)

        AvMessage message = new DefaultAvMessage.Builder(null)
                .build()

        when:
        component.sendMessage(message)

        then:
        1 * rabbitTemplate.send(_, _, _)
    }

    AvMessageListener getAVMessageListener() {
        return new AvMessageListener() {
            @Override
            void onAVMessage(AvMessage message) {

            }
        }
    }
}
