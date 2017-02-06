package dvoraka.avservice

import dvoraka.avservice.common.AvMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.configuration.ServiceConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 * CompositeMessageProcessor spec.
 */
@ContextConfiguration(classes = [ServiceConfig.class])
@ActiveProfiles(['core', 'storage', 'db'])
class CompositeMessageProcessorISpec extends Specification {

    @Autowired
    MessageProcessor checkAndFileProcessor

    BlockingQueue<AvMessage> queue
    AvMessageListener listener

    PollingConditions conditions


    def setup() {
        queue = new ArrayBlockingQueue<>(10)
        listener = new AvMessageListener() {
            @Override
            void onAvMessage(AvMessage message) {
                queue.put(message)
            }
        }

        checkAndFileProcessor.addProcessedAVMessageListener(listener)

        conditions = new PollingConditions(timeout: 2)
    }

    def "test file checking"() {
        given:
            AvMessage message = Utils.genInfectedMessage()

        when:
            checkAndFileProcessor.sendMessage(message)

        then:
            conditions.eventually {
                AvMessage response = queue.take()
                response != null
                response.getType() == MessageType.RESPONSE
                response.getVirusInfo() != Utils.OK_VIRUS_INFO
                response.getCorrelationId() == message.getId()
            }
    }

    def "test file saving with virus"() {
        given:
            AvMessage message = Utils.genInfectedFileMessage()

            AvMessage response = null
            AvMessageListener messageListener = new AvMessageListener() {
                @Override
                void onAvMessage(AvMessage m) {
                    response = m
                }
            }

            checkAndFileProcessor.addProcessedAVMessageListener(messageListener)

        when:
            checkAndFileProcessor.sendMessage(message)

        then:
            conditions.eventually {
                response != null
                response.getVirusInfo() != Utils.OK_VIRUS_INFO
                response.getCorrelationId() == message.getId()
            }
    }
}
