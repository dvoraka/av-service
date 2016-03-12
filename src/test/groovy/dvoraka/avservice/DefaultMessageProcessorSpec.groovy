package dvoraka.avservice

import dvoraka.avservice.data.DefaultAVMessage
import dvoraka.avservice.data.MessageStatus
import dvoraka.avservice.service.AVService
import spock.lang.Specification

/**
 * Default processor tests.
 */
class DefaultMessageProcessorSpec extends Specification {

    DefaultMessageProcessor processor = null;

    def cleanup() {
        if (processor != null) {
            processor.stop();
        }
    }

    def "processing message status"() {
        setup:
        String testId = "testId"

        AVService avService = Stub()
        avService.scanStream() >> {
            sleep(1000)
            return false
        }

        processor = new DefaultMessageProcessor(2);
        // TODO: improve it
        processor.avService = avService

        when:
        processor.sendMessage(new DefaultAVMessage.Builder(testId).build())

        then:
        processor.messageStatus(testId) == MessageStatus.PROCESSING
    }
}
