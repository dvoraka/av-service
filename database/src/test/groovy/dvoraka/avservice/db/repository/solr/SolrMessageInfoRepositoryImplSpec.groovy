package dvoraka.avservice.db.repository.solr

import dvoraka.avservice.db.model.MessageInfoDocument
import org.springframework.data.solr.core.SolrTemplate
import org.springframework.data.solr.core.mapping.SolrDocument
import spock.lang.Shared
import spock.lang.Specification

/**
 * Custom repo test.
 */
class SolrMessageInfoRepositoryImplSpec extends Specification {

    SolrMessageInfoRepositoryImpl messageInfoRepository
    SolrTemplate solrTemplate

    @Shared
    String collection = MessageInfoDocument.class
            .getAnnotation(SolrDocument.class).collection()


    def setup() {
        solrTemplate = Mock()
        messageInfoRepository = new SolrMessageInfoRepositoryImpl(solrTemplate)
    }

    def "saveSoft interactions"() {
        given:
            MessageInfoDocument document = new MessageInfoDocument()

        when:
            messageInfoRepository.saveSoft(document)

        then:
            1 * solrTemplate.saveBean(collection, document)
            1 * solrTemplate.softCommit(collection)
    }
}
