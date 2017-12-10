package dvoraka.avservice.client.amqp

import dvoraka.avservice.common.amqp.AvMessageMapper
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.listener.AvMessageListener
import dvoraka.avservice.common.util.Utils
import dvoraka.avservice.db.repository.db.DbMessageInfoRepository
import dvoraka.avservice.db.service.DbMessageInfoService
import org.springframework.amqp.AmqpException
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.MessageConversionException
import org.springframework.amqp.support.converter.MessageConverter
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

/**
 * AMQP adapter test.
 */
class AmqpAdapterSpec extends Specification {

    @Subject
    AmqpAdapter component

    RabbitTemplate rabbitTemplate
    AvMessageMapper messageMapper
    MessageConverter converter

    @Shared
    String testExchange = 'TEST-EXCHANGE'
    @Shared
    String testServiceId = 'testservice-1'


    def setup() {
        DbMessageInfoRepository infoRepository = Mock()
        DbMessageInfoService infoService = new DbMessageInfoService(infoRepository)

        converter = Mock()
        converter.fromMessage(_) >> Mock(AvMessage)

        rabbitTemplate = Mock()
        rabbitTemplate.getMessageConverter() >> converter

        component = new AmqpAdapter(testExchange, testServiceId, rabbitTemplate, infoService)

        messageMapper = new AvMessageMapper()
    }

    def "on message"() {
        given:
            AvMessageListener listener = Mock()
            AvMessage message = Utils.genMessage()
            Message amqpMsg = messageMapper.transform(message)

            component.addMessageListener(listener)

        when:
            component.onMessage(amqpMsg)

        then:
            1 * listener.onMessage(_)
    }

    def "on message with conversion exception"() {
        given:
            AvMessageListener listener = Mock()
            AvMessage message = Utils.genMessage()
            Message amqpMsg = messageMapper.transform(message)
            amqpMsg.getMessageProperties().setType(null)

            component.addMessageListener(listener)

        when:
            component.onMessage(amqpMsg)

        then:
            converter.fromMessage(amqpMsg) >> {
                throw new MessageConversionException("TEST conversion")
            }
            0 * listener.onMessage(_)
    }

    def "on message with null"() {
        when:
            component.onMessage((Message) null)

        then:
            thrown(NullPointerException)
    }

    def "send null message"() {
        when:
            component.sendMessage(null)

        then:
            thrown(NullPointerException)
    }

    def "send normal message"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            component.sendMessage(message)

        then:
            1 * rabbitTemplate.convertAndSend(_, _, _)
    }

    def "send message with conversion error"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            component.sendMessage(message)

        then:
            1 * rabbitTemplate.convertAndSend(testExchange, _ as String, message) >> {
                throw new MessageConversionException("Conversion problem")
            }

            1 * rabbitTemplate.convertAndSend(testExchange, _ as String, _)
    }

    def "send message with AMQP exception"() {
        given:
            AvMessage message = Utils.genMessage()

            rabbitTemplate.convertAndSend(testExchange, _ as String, message) >> {
                throw new AmqpException("Problem!")
            }

        when:
            component.sendMessage(message)

        then:
            notThrown(Exception)
    }

    def "add listeners"() {
        when:
            component.addMessageListener(getAvMessageListener())
            component.addMessageListener(getAvMessageListener())

        then:
            component.getListenerCount() == 2
    }

    def "remove listeners"() {
        given:
            AvMessageListener listener1 = getAvMessageListener()
            AvMessageListener listener2 = getAvMessageListener()

        when:
            component.addMessageListener(listener1)
            component.addMessageListener(listener2)

        then:
            component.getListenerCount() == 2

        when:
            component.removeMessageListener(listener1)
            component.removeMessageListener(listener2)

        then:
            component.getListenerCount() == 0
    }

    def "add listeners from diff threads"() {
        given:
            int observers = 50

            Runnable addListener = {
                component.addMessageListener(getAvMessageListener())
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
            observers == component.getListenerCount()
    }

    def "remove observers from different threads"() {
        given:
            int observers = 50

            AvMessageListener messageListener = getAvMessageListener()
            Runnable removeListener = {
                component.removeMessageListener(messageListener)
            }

            Thread[] threads = new Thread[observers]
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(removeListener)
            }

            observers.times {
                component.addMessageListener(messageListener)
            }

        when:
            threads.each {
                it.start()
            }
            threads.each {
                it.join()
            }

        then:
            component.getListenerCount() == 0
    }

    def "run wrong onMessage"() {
        when:
            component.onMessage((javax.jms.Message) null)

        then:
            thrown(UnsupportedOperationException)
    }

    def "check service ID"() {
        expect:
            component.getServiceId() == testServiceId
    }

    AvMessageListener getAvMessageListener() {
        return new AvMessageListener() {
            @Override
            void onMessage(AvMessage message) {
            }
        }
    }
}
