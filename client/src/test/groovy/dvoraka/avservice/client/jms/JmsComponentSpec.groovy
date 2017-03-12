package dvoraka.avservice.client.jms

import dvoraka.avservice.common.AvMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.db.service.MessageInfoService
import org.apache.activemq.command.ActiveMQMessage
import org.springframework.jms.IllegalStateException
import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.support.converter.MessageConversionException
import org.springframework.jms.support.converter.MessageConverter
import spock.lang.Specification
import spock.lang.Subject

import javax.jms.Message

/**
 * JMS component test.
 */
class JmsComponentSpec extends Specification {

    @Subject
    JmsComponent component

    JmsTemplate jmsTemplate
    String destination
    MessageConverter converter


    def setup() {
        destination = "TEST-DESTINATION"
        MessageInfoService messageInfoService = Mock()

        converter = Mock()

        jmsTemplate = Mock()
        jmsTemplate.getMessageConverter() >> converter

        component = new JmsComponent(destination, "TEST1", jmsTemplate, messageInfoService)
    }

    def "on message"() {
        given:
            AvMessageListener listener = Mock()
            AvMessage message = Utils.genMessage()
            converter.fromMessage(_) >> message

            component.addAvMessageListener(listener)

        when:
            component.onMessage(new ActiveMQMessage())

        then:
            1 * listener.onAvMessage(message)
    }

    def "on message with bad message"() {
        given:
            AvMessageListener listener = Mock()
            converter.fromMessage(_) >> {
                throw new MessageConversionException("TEST")
            }

            component.addAvMessageListener(listener)

        when:
            component.onMessage(new ActiveMQMessage())

        then:
            0 * listener.onAvMessage((AvMessage) _)
    }

    def "on message with null"() {
        when:
            component.onMessage((Message) null)

        then:
            thrown(NullPointerException)
    }

    def "send null message"() {
        when:
            component.sendAvMessage(null)

        then:
            thrown(NullPointerException)
    }

    def "send normal message"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            component.sendAvMessage(message)

        then:
            1 * jmsTemplate.convertAndSend(destination, message)
    }

    def "send message with conversion error"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            component.sendAvMessage(message)

        then:
            1 * jmsTemplate.convertAndSend(destination, message) >> {
                throw new MessageConversionException("Conversion problem")
            }

            1 * jmsTemplate.convertAndSend(destination, _)
    }

    def "send message with JMS exception"() {
        given:
            AvMessage message = Utils.genMessage()

            jmsTemplate.convertAndSend(destination, message) >> {
                throw new IllegalStateException(null)
            }

        when:
            component.sendAvMessage(message)

        then:
            notThrown(Exception)
    }

    def "add listeners"() {
        when:
            component.addAvMessageListener(getAvMessageListener())
            component.addAvMessageListener(getAvMessageListener())

        then:
            component.listenersCount() == 2
    }

    def "remove listeners"() {
        given:
            AvMessageListener listener1 = getAvMessageListener()
            AvMessageListener listener2 = getAvMessageListener()

        when:
            component.addAvMessageListener(listener1)
            component.addAvMessageListener(listener2)

        then:
            component.listenersCount() == 2

        when:
            component.removeAvMessageListener(listener1)
            component.removeAvMessageListener(listener2)

        then:
            component.listenersCount() == 0
    }

    def "add listeners from diff threads"() {
        given:
            int observers = 50

            Runnable addListener = {
                component.addAvMessageListener(getAvMessageListener())
            }

            Thread[] threads = new Thread[observers]
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(addListener)
            }

        when:
            threads.each {
                it.start()
            }
            threads.each {
                it.join()
            }

        then:
            observers == component.listenersCount()
    }

    def "remove observers from different threads"() {
        given:
            int observers = 50

            AvMessageListener messageListener = getAvMessageListener()
            Runnable removeListener = {
                component.removeAvMessageListener(messageListener)
            }

            Thread[] threads = new Thread[observers]
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(removeListener)
            }

            observers.times {
                component.addAvMessageListener(messageListener)
            }

        when:
            threads.each {
                it.start()
            }
            threads.each {
                it.join()
            }

        then:
            component.listenersCount() == 0
    }

    def "run wrong onMessage"() {
        when:
            component.onMessage((org.springframework.amqp.core.Message) null)

        then:
            thrown(UnsupportedOperationException)
    }

    AvMessageListener getAvMessageListener() {
        return new AvMessageListener() {
            @Override
            void onAvMessage(AvMessage message) {

            }
        }
    }
}
