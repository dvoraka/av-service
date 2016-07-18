package dvoraka.avservice.server.configuration

import dvoraka.avservice.MessageProcessor
import dvoraka.avservice.avprogram.AvProgram
import dvoraka.avservice.configuration.ServiceConfig
import dvoraka.avservice.server.AvServer
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
    AvServer avServer

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
            avService
    }

    def "AV program loading"() {
        expect:
            avProgram
    }

    def "AV server loading"() {
        expect:
            avServer
    }

    def "Message processor loading"() {
        expect:
            messageProcessor
    }

    def "Connection factory loading"() {
        expect:
            connectionFactory
    }

    def "AMQP admin loading"() {
        expect:
            amqpAdmin
    }

    def "Rabbit template loading"() {
        expect:
            rabbitTemplate
    }
}
