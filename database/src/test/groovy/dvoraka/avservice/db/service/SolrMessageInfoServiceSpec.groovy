package dvoraka.avservice.db.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageInfo
import dvoraka.avservice.common.data.AvMessageSource
import dvoraka.avservice.db.model.MessageInfoDocument
import dvoraka.avservice.db.repository.solr.SolrMessageInfoRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.Instant
import java.util.stream.Stream

/**
 * Service test.
 */
class SolrMessageInfoServiceSpec extends Specification {

    @Subject
    SolrMessageInfoService messageService

    SolrMessageInfoRepository messageRepository

    String testUuid = 'AAA'
    String testService = 'TEST'


    def setup() {
        messageRepository = Mock()
        messageService = new SolrMessageInfoService(messageRepository)
    }

    def "call save method without batching"() {
        given:
            AvMessage message = Utils.genNormalMessage()

        when:
            messageService.save(message, AvMessageSource.TEST, testService)

        then:
            1 * messageRepository.save(_)
    }

    def "call save method with batching"() {
        given:
            int batchSize = 2
            AvMessage message = Utils.genNormalMessage()
            messageService.enableBatching()
            messageService.setBatchSize(batchSize)

        expect:
            messageService.isBatching()
            messageService.getBatchSize() == batchSize

        when:
            messageService.save(message, AvMessageSource.TEST, testService)

        then:
            0 * messageRepository.save(_)
    }

    def "call save method with batching and reaching limit"() {
        given:
            int batchSize = 1
            AvMessage message = Utils.genNormalMessage()
            messageService.enableBatching()
            messageService.setBatchSize(batchSize)

        expect:
            messageService.isBatching()
            messageService.getBatchSize() == batchSize

        when:
            messageService.save(message, AvMessageSource.TEST, testService)

        then:
            1 * messageRepository.save(_ as Iterable)
    }

    def "load info"() {
        given:
            String uuid = testUuid

        when:
            messageService.loadInfo(uuid)

        then:
            1 * messageRepository.findByUuid(uuid) >> createTestDoc(uuid)
    }

    def "load info stream"() {
        given:
            String uuid = testUuid
            Instant now = Instant.now()
            List<MessageInfoDocument> docs = Arrays.asList(
                    createTestDoc(uuid), createTestDoc(uuid))

        when:
            Stream<AvMessageInfo> info = messageService.loadInfoStream(now, now)

        then:
            1 * messageRepository.findByCreatedBetween(_, _) >> docs
            info.count() == docs.size()
    }

    def "batching settings"() {
        expect:
            !messageService.isBatching()

        when:
            messageService.enableBatching()

        then:
            messageService.isBatching()

        when:
            messageService.disableBatching()

        then:
            !messageService.isBatching()
    }

    def "test stop with batching"() {
        given:
            messageService.enableBatching()

        when:
            messageService.save(Utils.genNormalMessage(), AvMessageSource.TEST, testService)

        then:
            0 * messageRepository.save(_ as MessageInfoDocument)

        when:
            messageService.stop()

        then:
            1 * messageRepository.save(_ as Iterable)
    }

    def "test stop without batching"() {
        when:
            messageService.save(Utils.genNormalMessage(), AvMessageSource.TEST, testService)

        then:
            1 * messageRepository.save(_ as MessageInfoDocument)

        when:
            messageService.stop()

        then:
            0 * messageRepository.save(_ as Iterable)
    }

    MessageInfoDocument createTestDoc(String uuid) {
        MessageInfoDocument testDoc = new MessageInfoDocument()
        testDoc.setUuid(uuid)
        testDoc.setSource(AvMessageSource.TEST.toString())
        testDoc.setCreated(new Date())

        return testDoc
    }
}
