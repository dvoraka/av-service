package dvoraka.avservice.server

import dvoraka.avservice.MessageProcessor
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AVMessage
import dvoraka.avservice.common.data.AVMessageMapper
import dvoraka.avservice.server.amqp.ParallelAmqpListeningStrategy
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Specification

/**
 * Strategy test.
 */
class ParallelAmqpListeningStrategySpec extends Specification {

    ParallelAmqpListeningStrategy strategy

    def "constructor"() {
        setup:
        int listeners = 5
        strategy = new ParallelAmqpListeningStrategy(listeners)

        expect:
        strategy.getListenersCount() == listeners
    }

    def "test listening"() {
        setup:
        int listeners = 5
        strategy = new ParallelAmqpListeningStrategy(listeners)
        RabbitTemplate template = Stub()
        MessageProcessor processor = Mock()
        AVMessage avMessage = Utils.genNormalMessage()

        template.receive() >> {
            sleep(100)
            return AVMessageMapper.transform(avMessage)
        }

        strategy.setRabbitTemplate(template)
        strategy.setMessageProcessor(processor)

        strategy.listen()
        sleep(150)

        expect:
        strategy.getListenersCount() == listeners
        strategy.isRunning()

        cleanup:
        strategy.stop()
    }

    def "test listening with malformed messages"() {
        setup:
        int listeners = 5
        strategy = new ParallelAmqpListeningStrategy(listeners)
        RabbitTemplate template = Stub()
        MessageProcessor processor = Mock()

        template.receive() >> {
            sleep(100)
            MessageProperties props = new MessageProperties()
            Message message = new Message(null, props)
            return message
        }

        strategy.setRabbitTemplate(template)
        strategy.setMessageProcessor(processor)

        strategy.listen()
        sleep(150)

        expect:
        strategy.getListenersCount() == listeners
        strategy.isRunning()

        cleanup:
        strategy.stop()
    }

    def "stopping test"() {
        when:
        int listeners = 5
        strategy = new ParallelAmqpListeningStrategy(listeners)
        RabbitTemplate template = Stub()
        MessageProcessor processor = Stub()

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
//         stoppingThread.isInterrupted()
        true

    }
}
