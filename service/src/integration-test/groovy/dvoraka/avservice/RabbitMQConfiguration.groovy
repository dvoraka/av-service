package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AVMessage
import dvoraka.avservice.common.data.AVMessageMapper
import dvoraka.avservice.configuration.ServiceConfig
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Configure RabbitMQ. Creates necessary queues and bindings.
 */
@Ignore
@ContextConfiguration(classes = [ServiceConfig.class])
@ActiveProfiles("amqp")
class RabbitMQConfiguration extends Specification {

    @Autowired
    RabbitTemplate rabbitTemplate


    def "send message"() {
        setup:
        AVMessage avMessage = Utils.genNormalMessage()
        Message message = AVMessageMapper.transform(avMessage)

        when:
        rabbitTemplate.sendAndReceive(message)

        then:
        notThrown(Exception)
    }
}
