package dvoraka.avservice.server

import dvoraka.avservice.MessageProcessor
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AVMessage
import dvoraka.avservice.common.data.AVMessageMapper
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Specification

/**
 * SimpleAmqpListeningStrategySpec test.
 */
class SimpleAmqpListeningStrategySpec extends Specification {

    SimpleAmqpListeningStrategy strategy


    def setup() {
        strategy = new SimpleAmqpListeningStrategy(500L)
    }

    def "listening test without a message"() {
        when:
        RabbitTemplate template = Stub()
        MessageProcessor processor = Mock()

        template.receive() >> {
            sleep(500)
        }

        strategy.setRabbitTemplate(template)
        strategy.setMessageProcessor(processor)

        new Thread(new Runnable() {
            @Override
            void run() {
                strategy.listen()
            }
        }).start()
        sleep(500)

        then:
        strategy.isRunning()

        when:
        strategy.stop()
        sleep(1000)

        then:
        !strategy.isRunning()
    }

    def "listening test with a message"() {
        when:
        RabbitTemplate template = Stub()
        MessageProcessor processor = Stub()
        AVMessage avMessage = Utils.genNormalMessage()
        template.receive() >> {
            sleep(100)
            return AVMessageMapper.transform(avMessage)
        }

        strategy.setRabbitTemplate(template)
        strategy.setMessageProcessor(processor)

        new Thread(new Runnable() {
            @Override
            void run() {
                strategy.listen()
            }
        }).start()
        sleep(500)

        then:
        strategy.isRunning()

        when:
        strategy.stop()
        sleep(1000)

        then:
        !strategy.isRunning()
    }

    def "listening test with a malformed message"() {
        when:
        RabbitTemplate template = Stub()
        MessageProcessor processor = Stub()
        AVMessage avMessage = Utils.genNormalMessage()
        template.receive() >> {
            sleep(100)
            MessageProperties props = new MessageProperties()
            Message message = new Message(null, props)
            return message
        }

        strategy.setRabbitTemplate(template)
        strategy.setMessageProcessor(processor)

        new Thread(new Runnable() {
            @Override
            void run() {
                strategy.listen()
            }
        }).start()
        sleep(500)

        then:
        strategy.isRunning()

        when:
        strategy.stop()
        sleep(1000)

        then:
        !strategy.isRunning()
    }

    def "stopping test"() {
        when:
        RabbitTemplate template = Stub()
        MessageProcessor processor = Stub()
        strategy = new SimpleAmqpListeningStrategy(10_000L)

        template.receive() >> {
            sleep(100)
        }

        strategy.setRabbitTemplate(template)
        strategy.setMessageProcessor(processor)

        Thread listeningThread = new Thread(new Runnable() {
            @Override
            void run() {
                strategy.listen()
            }
        })
        listeningThread.start()
        sleep(500)

        then:
        strategy.isRunning()

        when:
        Thread stoppingThread = new Thread(new Runnable() {
            @Override
            void run() {
                strategy.stop()
            }
        })
        stoppingThread.start()
        stoppingThread.interrupt()
        sleep(100)

        then:
        // TODO:
        // stoppingThread.isInterrupted()
        true

    }

    def "get listening timeout"() {
        setup:
        long timeout = 2000L
        strategy = new SimpleAmqpListeningStrategy(timeout)

        expect:
        strategy.getListeningTimeout() == timeout
    }
}
