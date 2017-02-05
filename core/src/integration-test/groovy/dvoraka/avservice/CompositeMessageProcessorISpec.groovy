package dvoraka.avservice

import dvoraka.avservice.common.AvMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.configuration.ServiceConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

/**
 * CompositeMessageProcessor spec.
 */
@ContextConfiguration(classes = [ServiceConfig.class])
@ActiveProfiles(['core', 'storage', 'db'])
class CompositeMessageProcessorISpec extends Specification {

    @Autowired
    MessageProcessor checkAndFileProcessor

    PollingConditions conditions


    def setup() {
        conditions = new PollingConditions(timeout: 2)

    }

    def "test file checking"() {
        given:
            AvMessage message = Utils.genInfectedMessage()

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

    def "test file saving with virus"() {
        given:
            AvMessage message = Utils.genFileMessage()

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
