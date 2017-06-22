package dvoraka.avservice.core

import dvoraka.avservice.avprogram.service.AvService
import dvoraka.avservice.common.AvMessageListener
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.common.data.MessageType
import dvoraka.avservice.common.exception.ScanException
import dvoraka.avservice.db.service.MessageInfoService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.util.concurrent.PollingConditions

import java.util.function.Predicate

/**
 * Default processor spec.
 */
class AvCheckMessageProcessorSpec extends Specification {

    @Subject
    AvCheckMessageProcessor processor

    AvService avService
    MessageInfoService infoService

    PollingConditions conditions
    @Shared
    Predicate<AvMessage> inputFilter = new Predicate<AvMessage>() {
        @Override
        boolean test(AvMessage message) {
            return (message.getType() == MessageType.FILE_CHECK
                    || message.getType() == MessageType.FILE_SAVE
                    || message.getType() == MessageType.FILE_UPDATE)
        }
    }
    @Shared
    String serviceId = 'TEST'


    def setup() {
        avService = Mock()
        infoService = Mock()

        processor = new AvCheckMessageProcessor(2, serviceId, avService, infoService)
        processor.setInputFilter(inputFilter)
        processor.start()

        conditions = new PollingConditions(timeout: 4)
    }

    def cleanup() {
        if (processor != null) {
            processor.stop()
        }
    }

    def "constructor (thread count, service ID, AV service, Message info service)"() {
        setup:
            int threadCount = 5
            processor = new AvCheckMessageProcessor(
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

            AvMessage message = Utils.genMessage()

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
                throw new ScanException("Service is dead")
            }

            AvMessage message = Utils.genMessage()

        when:
            processor.sendMessage(message)

        then:
            notThrown(ScanException)
    }

    def "send message with empty data"() {
        given:
            AvMessage message = new DefaultAvMessage.Builder('testId')
                    .type(MessageType.FILE_CHECK)
                    .build()

        when:
            processor.sendMessage(message)

        then:
            notThrown(ScanException)
            0 * avService._
    }

    def "send message with bad message type"() {
        given:
            AvMessage message = Utils.genDeleteMessage()

        when:
            processor.sendMessage(message)

        then:
            0 * avService._
            0 * infoService._
    }

    // still fuzzy timing
    def "processing message status"() {
        given:
            AvMessage message = Utils.genMessage()
            String testId = message.getId()

            avService.scanBytesWithInfo(_) >> {
                sleep(1000)
                return Utils.OK_VIRUS_INFO
            }

        when:
            processor.sendMessage(message)

        then:
            conditions.eventually {
                processor.messageStatus(testId) == MessageStatus.PROCESSING
            }
    }

    def "processed message status"() {
        given:
            AvMessage message = Utils.genMessage()

        when:
            processor.sendMessage(message)

        then:
            conditions.eventually {
                processor.messageStatus(message.getId()) == MessageStatus.PROCESSED
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
            AvMessage message = Utils.genMessage()
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
            int observers = 20

            Runnable addObserver = {
                processor.addProcessedAVMessageListener({})
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
            conditions.eventually {
                observers == processor.observersCount()
            }
    }

    def "remove observers from different threads"() {
        given:
            int observers = 20
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
            conditions.eventually {
                processor.observersCount() == 0
            }
    }
}
