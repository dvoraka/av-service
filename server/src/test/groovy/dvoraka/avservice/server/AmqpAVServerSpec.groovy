package dvoraka.avservice.server

import dvoraka.avservice.MessageProcessor
import dvoraka.avservice.common.ReceivingType
import dvoraka.avservice.server.amqp.AmqpAVServer
import spock.lang.Specification

/**
 * AMQP AV server test.
 */
class AmqpAVServerSpec extends Specification {

    AmqpAVServer amqpAVServer

    def setup() {
    }

    def "constructor"() {
        setup:
        ReceivingType receivingType = ReceivingType.LISTENER
        amqpAVServer = new AmqpAVServer(receivingType)

        expect:
        amqpAVServer.getReceivingType() == receivingType
    }

    def "start with polling"() {
        setup:
        MessageProcessor processor = Mock()
        ListeningStrategy strategy = Mock()
        ReceivingType receivingType = ReceivingType.POLLING
        amqpAVServer = new AmqpAVServer(receivingType)

        amqpAVServer.setMessageProcessor(processor)
        amqpAVServer.setListeningStrategy(strategy)

        amqpAVServer.start()
        sleep(100)

        expect:
        amqpAVServer.isStarted()
        amqpAVServer.isRunning()
        amqpAVServer.getReceivingType() == receivingType

        cleanup:
        amqpAVServer.stop()
    }

    def "start with listening"() {
        setup:
        MessageProcessor processor = Mock()
        ListeningStrategy strategy = Mock()
        ReceivingType receivingType = ReceivingType.LISTENER
        amqpAVServer = new AmqpAVServer(receivingType)

        amqpAVServer.setMessageProcessor(processor)
        amqpAVServer.setListeningStrategy(strategy)

        amqpAVServer.start()
        sleep(100)

        expect:
        amqpAVServer.isStarted()
        amqpAVServer.isRunning()
        amqpAVServer.getReceivingType() == receivingType

        cleanup:
        amqpAVServer.stop()
    }
}
