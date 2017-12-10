package dvoraka.avservice.core

import dvoraka.avservice.common.AvMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.core.configuration.CoreConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

/**
 * CompositeMessageProcessor spec.
 */
@ContextConfiguration(classes = [CoreConfig.class])
@DirtiesContext
@ActiveProfiles(['core', 'storage', 'db'])
class CompositeMessageProcessorISpec extends Specification {

    @Autowired
    MessageProcessor messageProcessor

    BlockingQueue<AvMessage> queue
    AvMessageListener listener

    PollingConditions conditions
    long pollingTimeout = 4


    def setup() {
        queue = new ArrayBlockingQueue<>(10)
        listener = new AvMessageListener() {
            @Override
            void onMessage(AvMessage message) {
                queue.put(message)
            }
        }

        messageProcessor.addProcessedAVMessageListener(listener)

        conditions = new PollingConditions(timeout: 2)
    }

    def "test file checking"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            messageProcessor.sendMessage(message)

        then:
            conditions.eventually {
                AvMessage response = queue.poll(pollingTimeout, TimeUnit.SECONDS)
                response != null
                response.getType() == MessageType.RESPONSE
                response.getVirusInfo() == Utils.OK_VIRUS_INFO
                response.getCorrelationId() == message.getId()
            }
    }

    def "test file checking with virus"() {
        given:
            AvMessage message = Utils.genInfectedMessage()

        when:
            messageProcessor.sendMessage(message)

        then:
            conditions.eventually {
                AvMessage response = queue.poll(pollingTimeout, TimeUnit.SECONDS)
                response != null
                response.getType() == MessageType.RESPONSE
                response.getVirusInfo() != Utils.OK_VIRUS_INFO
                response.getCorrelationId() == message.getId()
            }
    }

    def "test file saving"() {
        given:
            AvMessage message = Utils.genFileMessage()

        when:
            messageProcessor.sendMessage(message)

        then:
            conditions.eventually {
                AvMessage response = queue.poll(pollingTimeout, TimeUnit.SECONDS)
                response != null
                response.getType() == MessageType.FILE_RESPONSE
                response.getCorrelationId() == message.getId()
            }
    }

    def "test file saving with virus"() {
        given:
            AvMessage message = Utils.genInfectedFileMessage()

        when:
            messageProcessor.sendMessage(message)

        then:
            conditions.eventually {
                AvMessage response = queue.poll(pollingTimeout, TimeUnit.SECONDS)
                response != null
                response.getType() == MessageType.RESPONSE
                response.getVirusInfo() != Utils.OK_VIRUS_INFO
                response.getCorrelationId() == message.getId()
            }
    }

    def "save and update"() {
        given:
            AvMessage message = Utils.genFileMessage()
            AvMessage update = new DefaultAvMessage.Builder(Utils.genUuidString())
                    .data(new byte[2])
                    .type(MessageType.FILE_UPDATE)
                    .filename(message.getFilename())
                    .owner(message.getOwner())
                    .build();

        when:
            messageProcessor.sendMessage(message)

        then:
            conditions.eventually {
                AvMessage response = queue.poll(pollingTimeout, TimeUnit.SECONDS)
                response != null
                response.getType() == MessageType.FILE_RESPONSE
                response.getCorrelationId() == message.getId()
            }

        when:
            messageProcessor.sendMessage(update)

        then:
            conditions.eventually {
                AvMessage response = queue.poll(pollingTimeout, TimeUnit.SECONDS)
                response != null
                response.getType() == MessageType.FILE_RESPONSE
                response.getCorrelationId() == update.getId()
            }
    }
}
