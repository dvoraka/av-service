package dvoraka.avservice

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AVMessage
import dvoraka.avservice.common.data.DefaultAVMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.exception.ScanErrorException
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
            processor.cleanup()
        }
    }

    def "constructor (thread count)"() {
        setup:
        int threadCount = 5
        processor = new DefaultMessageProcessor(threadCount)

        expect:
        processor.getThreadCount() == threadCount
        processor.getQueueSize() == DefaultMessageProcessor.DEFAULT_QUEUE_SIZE
        processor.getServerReceivingType() ==
                DefaultMessageProcessor.DEFAULT_RECEIVING_TYPE
    }

    def "send normal message"() {
        setup:
        AVService service = Stub()
        service.scanStream(_) >> false

        processor.setAvService(service)

        AVMessage message = Utils.genNormalMessage()
        processor.sendMessage(message)

        sleep(1000)

        expect:
        processor.hasProcessedMessage()
        processor.getProcessedMessage().getCorrelationId().equals(message.getId())
    }

    def "send message (with a service error)"() {
        setup:
        AVService service = Stub()
        service.scanStream(_) >> {
            throw new ScanErrorException("Service is dead")
        }

        processor.setAvService(service)
        AVMessage message = Utils.genNormalMessage()

        processor.sendMessage(message)

        sleep(1000)

        expect:
        // TODO: catch an exception
        true
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
