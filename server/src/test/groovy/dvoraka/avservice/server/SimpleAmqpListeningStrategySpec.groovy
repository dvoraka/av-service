package dvoraka.avservice.server

import dvoraka.avservice.MessageProcessor
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageMapper
import dvoraka.avservice.server.amqp.SimpleAmqpListeningStrategy
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

/**
 * SimpleAmqpListeningStrategySpec test.
 */
class SimpleAmqpListeningStrategySpec extends Specification {

    SimpleAmqpListeningStrategy strategy
    PollingConditions conditions


    def setup() {
        strategy = new SimpleAmqpListeningStrategy(500L)
        conditions = new PollingConditions(timeout: 2)
    }

    def "listening test without a message"() {
        when:
        RabbitTemplate template = Stub()
        MessageProcessor processor = Mock()

        template.receive() >> {
            sleep(500)
        }

        ReflectionTestUtils.setField(strategy, null, template, RabbitTemplate.class)
        ReflectionTestUtils.setField(strategy, null, processor, MessageProcessor.class)

        new Thread(new Runnable() {
            @Override
            void run() {
                strategy.listen()
            }
        }).start()

        then:
        conditions.eventually {
            strategy.isRunning()
        }

        when:
        strategy.stop()

        then:
        conditions.eventually {
            !strategy.isRunning()
        }
    }

    def "listening test with a message"() {
        when:
        RabbitTemplate template = Stub()
        MessageProcessor processor = Stub()
        AvMessage avMessage = Utils.genNormalMessage()

        template.receive() >> {
            sleep(100)
            return AvMessageMapper.transform(avMessage)
        }

        ReflectionTestUtils.setField(strategy, null, template, RabbitTemplate.class)
        ReflectionTestUtils.setField(strategy, null, processor, MessageProcessor.class)

        new Thread(new Runnable() {
            @Override
            void run() {
                strategy.listen()
            }
        }).start()

        then:
        conditions.eventually {
            strategy.isRunning()
        }

        when:
        strategy.stop()

        then:
        conditions.eventually {
            !strategy.isRunning()
        }
    }

    def "listening test with a malformed message"() {
        when:
        RabbitTemplate template = Stub()
        MessageProcessor processor = Stub()

        template.receive() >> {
            sleep(100)
            MessageProperties props = new MessageProperties()
            Message message = new Message(null, props)
            return message
        }

        ReflectionTestUtils.setField(strategy, null, template, RabbitTemplate.class)
        ReflectionTestUtils.setField(strategy, null, processor, MessageProcessor.class)

        new Thread(new Runnable() {
            @Override
            void run() {
                strategy.listen()
            }
        }).start()

        then:
        conditions.eventually {
            strategy.isRunning()
        }

        when:
        strategy.stop()

        then:
        conditions.eventually {
            !strategy.isRunning()
        }
    }

    def "stopping test with interruption"() {
        when:
        RabbitTemplate template = Stub()
        MessageProcessor processor = Stub()
        strategy = new SimpleAmqpListeningStrategy(10_000L)

        template.receive() >> {
            sleep(100)
        }

        ReflectionTestUtils.setField(strategy, null, template, RabbitTemplate.class)
        ReflectionTestUtils.setField(strategy, null, processor, MessageProcessor.class)

        Thread listeningThread = new Thread(new Runnable() {
            @Override
            void run() {
                strategy.listen()
            }
        })
        listeningThread.start()

        then:
        conditions.eventually {
            strategy.isRunning()
        }

        when:
        Thread stoppingThread = new Thread(new Runnable() {
            @Override
            void run() {
                strategy.stop()
            }
        })
        stoppingThread.start()
        stoppingThread.interrupt()

        then:
        conditions.eventually {
            !strategy.isRunning()
            stoppingThread.getState() == Thread.State.TERMINATED
        }
    }

    def "get listening timeout"() {
        setup:
        long timeout = 2000L
        strategy = new SimpleAmqpListeningStrategy(timeout)

        expect:
        strategy.getListeningTimeout() == timeout
    }
}
