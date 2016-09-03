package dvoraka.avservice.server.configuration

import dvoraka.avservice.MessageProcessor
import dvoraka.avservice.server.AvServer
import dvoraka.avservice.server.ListeningStrategy
import dvoraka.avservice.server.ServerComponent
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.MessageListener
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * AMQP Spring configuration test.
 */
@ContextConfiguration(classes = [AmqpConfig.class])
@ActiveProfiles(["amqp", "database"])
class AmqpConfigISpec extends Specification {

    @Autowired
    AvServer avServer

    @Autowired
    ServerComponent serverComponent

    @Autowired
    MessageProcessor messageProcessor

    @Autowired
    SimpleMessageListenerContainer messageListenerContainer

    @Autowired
    ListeningStrategy listeningStrategy

    @Autowired
    ConnectionFactory connectionFactory

    @Autowired
    AmqpAdmin amqpAdmin

    @Autowired
    RabbitTemplate rabbitTemplate

    @Autowired
    MessageListener messageListener

    @Autowired
    Queue checkQueue

    @Autowired
    Queue resultQueue

    @Autowired
    FanoutExchange checkExchange

    @Autowired
    FanoutExchange resultExchange

    @Autowired
    Binding bindingCheck

    @Autowired
    Binding bindingResult


    def "AvServer loading"() {
        expect:
            avServer
    }

    def "ServerComponent loading"() {
        expect:
            serverComponent
    }

    def "MessageProcessor loading"() {
        expect:
            messageProcessor
    }

    def "SimpleMessageListenerContainer loading"() {
        expect:
            messageListenerContainer
    }

    def "ListeningStrategy loading"() {
        expect:
            listeningStrategy
    }

    def "ConnectionFactory loading"() {
        expect:
            connectionFactory
    }

    def "AmqpAdmin loading"() {
        expect:
            amqpAdmin
    }

    def "RabbitTemplate loading"() {
        expect:
            rabbitTemplate
    }

    def "MessageListener loading"() {
        expect:
            messageListener
    }

    def "Queue check loading"() {
        expect:
            checkQueue
    }

    def "Queue result loading"() {
        expect:
            resultQueue
    }

    def "FanoutExchange check loading"() {
        expect:
            checkExchange
    }

    def "FanoutExchange result loading"() {
        expect:
            resultExchange
    }

    def "Binding check loading"() {
        expect:
            bindingCheck
    }

    def "Binding result loading"() {
        expect:
            bindingResult
    }
}
