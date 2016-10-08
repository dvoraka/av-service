package dvoraka.avservice.db.configuration

import dvoraka.avservice.db.model.MessageInfoDocument
import dvoraka.avservice.db.repository.SolrMessageInfoRepository
import org.apache.solr.client.solrj.SolrClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Solr testing.
 */
@ContextConfiguration(classes = [SolrConfig.class])
class SolrConfigITest extends Specification {

    @Autowired
    SolrClient solrClient
    @Autowired
    SolrMessageInfoRepository messageInfoRepository


    def "ping Solr"() {
        expect:
            solrClient.ping()
    }

    def "save document"() {
        setup:
            MessageInfoDocument messageInfo = new MessageInfoDocument()
            messageInfo.setId(UUID.randomUUID().toString())
            messageInfo.setUuid(UUID.randomUUID().toString())
            messageInfo.setCreated(new Date())
            messageInfo.setServiceId("TEST")
            messageInfo.setSource("CONFIG-TEST")

        expect:
            messageInfoRepository.save(messageInfo)
    }
}
