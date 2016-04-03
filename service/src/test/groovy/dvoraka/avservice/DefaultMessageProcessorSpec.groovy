package dvoraka.avservice

import dvoraka.avservice.common.data.DefaultAVMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.service.AVService
import spock.lang.Specification

/**
 * Default processor tests.
 */
class DefaultMessageProcessorSpec extends Specification {

    DefaultMessageProcessor processor


    def setup() {
        processor = new DefaultMessageProcessor(2)
    }

    def cleanup() {
        if (processor != null) {
            processor.stop()
        }
    }

    def "processing message status"() {
        setup:
        String testId = "testId"

        AVService avService = Stub()
        avService.scanStream(_) >> {
            sleep(1000)
            return false
        }

        processor.setAvService(avService)

        when:
        processor.sendMessage(new DefaultAVMessage.Builder(testId).build())

        then:
        processor.messageStatus(testId) == MessageStatus.PROCESSING
    }

    def "processed message status"() {
        setup:
        String testId = "testId"

        AVService avService = Stub()
        processor.setAvService(avService)

        when:
        processor.sendMessage(new DefaultAVMessage.Builder(testId).build())
        sleep(1000)

        then:
        processor.messageStatus(testId) == MessageStatus.PROCESSED
    }

    def "unknown message status"() {
        setup:
        String testId = "testId"

        AVService avService = Stub()
        processor.setAvService(avService)

        expect:
        processor.messageStatus(testId) == MessageStatus.UNKNOWN
    }
}
