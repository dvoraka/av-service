package dvoraka.avservice.client.amqp

import dvoraka.avservice.common.ReplicationMessageListener
import dvoraka.avservice.common.data.replication.DefaultReplicationMessage
import dvoraka.avservice.common.data.replication.ReplicationMessage
import dvoraka.avservice.common.helper.FileServiceHelper
import dvoraka.avservice.common.helper.replication.ReplicationHelper
import dvoraka.avservice.common.helper.replication.ReplicationServiceHelper
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.MessageConversionException
import org.springframework.amqp.support.converter.MessageConverter
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

/**
 * AmqpReplicationComponent spec.
 */
class AmqpReplicationComponentSpec extends Specification implements
        ReplicationHelper, ReplicationServiceHelper, FileServiceHelper {

    @Subject
    AmqpReplicationComponent component

    RabbitTemplate rabbitTemplate
    MessageConverter converter

    @Shared
    String nodeId = 'testId'


    def setup() {
        converter = Mock()

        rabbitTemplate = Mock()
        rabbitTemplate.getMessageConverter() >> converter

        component = new AmqpReplicationComponent(rabbitTemplate, nodeId, 'broadcast')
    }

    def "on message"() {
        given:
            ReplicationMessageListener listener = Mock()
            component.addReplicationMessageListener(listener)

            Message amqpMsg = new Message(new byte[0], new MessageProperties())
            ReplicationMessage message = new DefaultReplicationMessage.Builder(null).build()

            converter.fromMessage(amqpMsg) >> message

        when:
            component.onMessage(amqpMsg)

        then:
            1 * listener.onMessage(message)
    }

    def "on message with conversion exception"() {
        given:
            ReplicationMessageListener listener = Mock()
            component.addReplicationMessageListener(listener)

            Message amqpMsg = new Message(new byte[0], new MessageProperties())
            converter.fromMessage(amqpMsg) >> { throw new MessageConversionException('Error') }

        when:
            component.onMessage(amqpMsg)

        then:
            notThrown(Exception)
            0 * listener._
    }

    def "on message without listener"() {
        given:
            ReplicationMessageListener listener = Mock()
            component.addReplicationMessageListener(listener)

            Message amqpMsg = new Message(new byte[0], new MessageProperties())
            ReplicationMessage message = new DefaultReplicationMessage.Builder(null).build()

            converter.fromMessage(amqpMsg) >> message

        when:
            component.removeReplicationMessageListener(listener)
            component.onMessage(amqpMsg)

        then:
            0 * listener.onMessage(message)
    }

    def "send message"() {
        when:
            component.sendMessage(createDiscoverRequest(nodeId))

        then:
            1 * rabbitTemplate._
        when:
            component.sendMessage(createSuccessResponse(
                    createSaveMessage(
                            fileSaveMessage('test', 'test', null),
                            nodeId,
                            'test'),
                    nodeId))

        then:
            1 * rabbitTemplate._
    }
}
