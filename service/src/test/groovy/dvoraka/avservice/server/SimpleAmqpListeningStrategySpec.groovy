package dvoraka.avservice.server

import dvoraka.avservice.MessageProcessor
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AVMessage
import dvoraka.avservice.common.data.AVMessageMapper
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

    def "get listening timeout"() {
        setup:
        long timeout = 2000L
        strategy = new SimpleAmqpListeningStrategy(timeout)

        expect:
        strategy.getListeningTimeout() == timeout
    }
}
