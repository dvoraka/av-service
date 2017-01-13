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


    def setup() {
        messageRepository = Mock()
        messageService = new SolrMessageInfoService(messageRepository)
    }

    def "call save method without batching"() {
        given:
            AvMessage message = Utils.genNormalMessage()

        when:
            messageService.save(message, AvMessageSource.TEST, "test")

        then:
            1 * messageRepository.save(_)
    }

    def "call save method with batching"() {
        given:
            AvMessage message = Utils.genNormalMessage()
            messageService.enableBatching()

        expect:
            messageService.isBatching()

        when:
            messageService.save(message, AvMessageSource.TEST, "test")

        then:
            notThrown(Exception)
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

    MessageInfoDocument createTestDoc(String uuid) {
        MessageInfoDocument testDoc = new MessageInfoDocument()
        testDoc.setUuid(uuid)
        testDoc.setSource(AvMessageSource.TEST.toString())
        testDoc.setCreated(new Date())

        return testDoc
    }
}
