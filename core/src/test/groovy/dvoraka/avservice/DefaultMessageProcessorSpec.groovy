package dvoraka.avservice

import dvoraka.avservice.common.AvMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.common.exception.ScanErrorException
import dvoraka.avservice.db.service.MessageInfoService
import dvoraka.avservice.service.AvService
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject
import spock.util.concurrent.PollingConditions

/**
 * Default processor spec.
 */
class DefaultMessageProcessorSpec extends Specification {

    @Subject
    DefaultMessageProcessor processor

    AvService avService
    MessageInfoService infoService

    PollingConditions conditions
    String serviceId = 'TEST'


    def setup() {
        avService = Mock()
        infoService = Mock()

        processor = new DefaultMessageProcessor(2, serviceId, avService, infoService)
        processor.start()

        conditions = new PollingConditions(timeout: 2)
    }

    def cleanup() {
        if (processor != null) {
            processor.cleanup()
        }
    }

    def "constructor (thread count, service ID, AV service, Message info service)"() {
        setup:
            int threadCount = 5
            processor = new DefaultMessageProcessor(
                    threadCount,
                    serviceId,
                    Mock(AvService),
                    Mock(MessageInfoService)
            )

        expect:
            processor.getThreadCount() == threadCount
    }

    def "responding test with a listener"() {
        given:
            avService.scanBytesWithInfo((byte[]) _) >> Utils.OK_VIRUS_INFO

            AvMessage response = null
            AvMessageListener messageListener = new AvMessageListener() {
                @Override
                void onAvMessage(AvMessage message) {
                    response = message
                }
            }

            processor.addProcessedAVMessageListener(messageListener)
            processor.start()

            AvMessage message = Utils.genNormalMessage()

        when:
            processor.sendMessage(message)

        then:
            conditions.eventually {
                response != null
                response.getCorrelationId() == message.getId()
            }
    }

    def "add and remove listeners"() {
        given:
            AvMessageListener messageListener = Mock()
            processor.start()

        expect:
            processor.observersCount() == 0

        when:
            processor.addProcessedAVMessageListener(messageListener)

        then:
            processor.observersCount() == 1

        when:
            processor.removeProcessedAVMessageListener(messageListener)

        then:
            processor.observersCount() == 0
    }

    def "send message with a service error"() {
        given:
            avService.scanBytesWithInfo(_) >> {
                throw new ScanErrorException("Service is dead")
            }

            AvMessage message = Utils.genNormalMessage()

        when:
            processor.sendMessage(message)

        then:
            notThrown(ScanErrorException)
    }

    @Ignore("Too fuzzy timing")
    def "processing message status"() {
        given:
            String testId = "testId"

            AvService service = Stub()
            service.scanBytes(_) >> {
                sleep(5000)
                return false
            }

            setProcessorService(service)

        when:
            processor.sendMessage(new DefaultAvMessage.Builder(testId)
                    .data(new byte[0])
                    .build())

        then:
            processor.messageStatus(testId) == MessageStatus.PROCESSING
    }

    def "processed message status"() {
        given:
            String testId = "testId"

        when:
            processor.sendMessage(new DefaultAvMessage.Builder(testId).build())

        then:
            conditions.eventually {
                processor.messageStatus(testId) == MessageStatus.PROCESSED
            }
    }

    def "unknown message status"() {
        setup:
            String testId = "testId"

        expect:
            processor.messageStatus(testId) == MessageStatus.UNKNOWN
    }

    def "test message counters"() {
        given:
            avService.scanBytesWithInfo(_) >> Utils.OK_VIRUS_INFO

        when:
            AvMessage message = Utils.genNormalMessage()
            messageCount.times {
                processor.sendMessage(message)
            }

        then:
            conditions.eventually {
                processor.getReceivedMsgCount() == messageCount
                processor.getProcessedMsgCount() == messageCount
            }

        where:
            messageCount << [1, 3]
    }

    def "add observers from different threads"() {
        given:
            int observers = 50
            AvMessageListener messageListener = {}

            Runnable addObserver = {
                processor.addProcessedAVMessageListener(messageListener)
            }

            Thread[] threads = new Thread[observers]
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(addObserver)
            }

        when:
            threads.each {
                it.start()
            }
            threads.each {
                it.join()
            }

        then:
            observers == processor.observersCount()
    }

    def "remove observers from different threads"() {
        given:
            int observers = 50
            AvMessageListener messageListener = {}

            Runnable removeObserver = {
                processor.removeProcessedAVMessageListener(messageListener)
            }

            Thread[] threads = new Thread[observers]
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(removeObserver)
            }

            observers.times {
                processor.addProcessedAVMessageListener(messageListener)
            }

        when:
            threads.each {
                it.start()
            }
            threads.each {
                it.join()
            }

        then:
            processor.observersCount() == 0
    }
}
