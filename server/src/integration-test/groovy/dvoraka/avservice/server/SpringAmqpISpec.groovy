package dvoraka.avservice.server

import dvoraka.avservice.MessageProcessor
import dvoraka.avservice.avprogram.AvProgram
import dvoraka.avservice.configuration.ServiceConfig
import dvoraka.avservice.service.AvService
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * DI config test for profile "amqp".
 */
@Ignore("new modules structure")
@ContextConfiguration(classes = [ServiceConfig.class])
@ActiveProfiles("amqp")
class SpringAmqpISpec extends Specification {

    @Autowired
    AvService avService

    @Autowired
    AvProgram avProgram

    @Autowired
    AVServer avServer

    @Autowired
    MessageProcessor messageProcessor

    @Autowired
    ConnectionFactory connectionFactory

    @Autowired
    AmqpAdmin amqpAdmin

    @Autowired
    RabbitTemplate rabbitTemplate


    def "AV service loading"() {
        expect:
        avService != null
    }

    def "AV program loading"() {
        expect:
        avProgram != null
    }

    def "AV server loading"() {
        expect:
        avServer != null
    }

    def "Message processor loading"() {
        expect:
        messageProcessor != null
    }

    def "Connection factory loading"() {
        expect:
        connectionFactory != null
    }

    def "AMQP admin loading"() {
        expect:
        amqpAdmin != null
    }

    def "Rabbit template loading"() {
        expect:
        rabbitTemplate != null
    }
}
