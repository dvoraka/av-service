package dvoraka.avservice.db.service

import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.data.AvMessageSource
import dvoraka.avservice.db.repository.solr.SolrMessageInfoRepository
import spock.lang.Specification

/**
 * Service test.
 */
class SolrMessageInfoServiceSpec extends Specification {

    SolrMessageInfoService messageService
    SolrMessageInfoRepository messageRepository


    def setup() {
        messageRepository = Mock()
        messageService = new SolrMessageInfoService(messageRepository)
    }

    def "call save method"() {
        given:
            AvMessage message = Utils.genNormalMessage()

        when:
            messageService.save(message, AvMessageSource.TEST, "test")

        then:
            notThrown(Exception)
    }
}
