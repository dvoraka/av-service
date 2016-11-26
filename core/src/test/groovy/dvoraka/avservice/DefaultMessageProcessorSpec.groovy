package dvoraka.avservice

import dvoraka.avservice.common.ProcessedAvMessageListener
import dvoraka.avservice.common.ReceivingType
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.DefaultAvMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.common.exception.ScanErrorException
import dvoraka.avservice.db.service.MessageInfoService
import dvoraka.avservice.service.AvService
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Ignore
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

/**
 * Default processor tests.
 */
class DefaultMessageProcessorSpec extends Specification {

    DefaultMessageProcessor processor
    PollingConditions conditions
    String serviceId = 'TEST'


    def setup() {
        processor = new DefaultMessageProcessor(2, serviceId)
        setMessageInfoService(Mock(MessageInfoService))

        processor.start()
        conditions = new PollingConditions(timeout: 2)
    }

    def cleanup() {
        if (processor != null) {
            processor.cleanup()
        }
    }

    void setMessageInfoService(MessageInfoService service) {
        ReflectionTestUtils.setField(processor, null, service, MessageInfoService.class)
    }

    void setProcessorService(AvService service) {
        ReflectionTestUtils.setField(processor, null, service, AvService.class)
    }

    def "constructor (thread count)"() {
        setup:
            int threadCount = 5
            processor = new DefaultMessageProcessor(threadCount, serviceId)

        expect:
            processor.getThreadCount() == threadCount
            processor.getQueueSize() == DefaultMessageProcessor.DEFAULT_QUEUE_SIZE
            processor.getServerReceivingType() ==
                    DefaultMessageProcessor.DEFAULT_RECEIVING_TYPE
    }

    def "constructor (thread count, rec. type, queue size)"() {
        setup:
            int threadCount = 5
            ReceivingType receivingType = ReceivingType.LISTENER
            int queueSize = 10
            processor = new DefaultMessageProcessor(
                    threadCount,
                    receivingType,
                    queueSize,
                    serviceId)

        expect:
            processor.getThreadCount() == threadCount
            processor.getServerReceivingType() == receivingType
            processor.getQueueSize() == queueSize
    }

    def "send normal message"() {
        given:
            AvService service = Stub()
            service.scanBytesWithInfo(_) >> Utils.OK_VIRUS_INFO
            setProcessorService(service)

            AvMessage message = Utils.genNormalMessage()

        when:
            processor.sendMessage(message)

        then:
            conditions.eventually {
                processor.hasProcessedMessage()
            }

        and:
            AvMessage resultMessage = processor.getProcessedMessage()
            resultMessage.getCorrelationId() == message.getId()
            resultMessage.getVirusInfo() == Utils.OK_VIRUS_INFO
    }

    def "send message without data"() {
        given:
            AvMessage message = new DefaultAvMessage.Builder(null)
                    .data(null)
                    .build()

        when:
            processor.sendMessage(message)

        then:
            conditions.eventually {
                processor.hasProcessedMessage()
            }

        and:
            AvMessage resultMessage = processor.getProcessedMessage()
            resultMessage.getCorrelationId() == message.getId()
            //TODO: Check error message after its implementation
            resultMessage.getData()
    }

    def "responding test with a listener"() {
        given:
            AvService service = Stub()
            service.scanBytes(_) >> false

            AvMessage response = null
            ProcessedAvMessageListener messageListener = new ProcessedAvMessageListener() {
                @Override
                void onProcessedAvMessage(AvMessage message) {
                    response = message
                }
            }

            processor = new DefaultMessageProcessor(2, ReceivingType.LISTENER, 10, serviceId)
            setProcessorService(service)
            setMessageInfoService(Mock(MessageInfoService))
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
            processor = new DefaultMessageProcessor(2, ReceivingType.LISTENER, 10, serviceId)
            ProcessedAvMessageListener messageListener = Mock()
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

    def "add a listener for polling type"() {
        when:
            processor.addProcessedAVMessageListener(null)

        then:
            thrown(UnsupportedOperationException)
    }

    def "ask for a message with a listener"() {
        given:
            processor = new DefaultMessageProcessor(2, ReceivingType.LISTENER, 10, serviceId)
            processor.start()

        when:
            processor.hasProcessedMessage()

        then:
            thrown(UnsupportedOperationException)

        when:
            processor.getProcessedMessage()

        then:
            thrown(UnsupportedOperationException)
    }

    def "send message with a full queue"() {
        setup:
            AvService service = Stub()
            service.scanBytes(_) >> false

            processor = new DefaultMessageProcessor(2, ReceivingType.POLLING, 1, serviceId)
            setProcessorService(service)
            setMessageInfoService(Mock(MessageInfoService))
            processor.start()

            AvMessage message1 = Utils.genNormalMessage()
            processor.sendMessage(message1)
            AvMessage message2 = Utils.genNormalMessage()
            processor.sendMessage(message2)

        expect:
            conditions.eventually {
                processor.isProcessedQueueFull()
                processor.hasProcessedMessage()
                processor.getProcessedMessage()
            }

        and:
            conditions.eventually {
                processor.hasProcessedMessage()
                processor.getProcessedMessage()
            }
            conditions.eventually {
                !processor.isProcessedQueueFull()
            }
    }

    def "send message with a service error"() {
        given:
            AvService service = Stub()
            service.scanBytesWithInfo(_) >> {
                throw new ScanErrorException("Service is dead")
            }

            setProcessorService(service)
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

            AvService service = Stub()
            setProcessorService(service)

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

            AvService service = Stub()
            setProcessorService(service)

        expect:
            processor.messageStatus(testId) == MessageStatus.UNKNOWN
    }

    def "test message counters"() {
        given:
            AvService service = Stub()
            service.scanBytes(_) >> false

            setProcessorService(service)

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
            ProcessedAvMessageListener messageListener = {}

            processor = new DefaultMessageProcessor(2, ReceivingType.LISTENER, 10, serviceId)
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
            ProcessedAvMessageListener messageListener = {}

            processor = new DefaultMessageProcessor(2, ReceivingType.LISTENER, 10, serviceId)
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
