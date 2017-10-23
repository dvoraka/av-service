package dvoraka.avservice.db.configuration

import dvoraka.avservice.db.model.MessageInfoDocument
import dvoraka.avservice.db.repository.solr.SolrMessageInfoRepository
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.common.SolrDocument
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.solr.core.SolrTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification

/**
 * Solr testing.
 */
@ContextConfiguration(classes = [SolrConfig.class])
@ActiveProfiles(['db-solr'])
class SolrConfigISpec extends Specification {

    @Autowired
    SolrTemplate solrTemplate
    @Autowired
    SolrMessageInfoRepository messageInfoRepository

    @Shared
    String collection = MessageInfoDocument.class
            .getAnnotation(SolrDocument.class).collection()
    @Shared
    String solrBase = 'http://localhost:8983/solr/'


    def "ping Solr"() {
        given:
            SolrClient solrClient = new HttpSolrClient.Builder()
                    .withBaseSolrUrl(solrBase + collection + '/')
                    .build();

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
