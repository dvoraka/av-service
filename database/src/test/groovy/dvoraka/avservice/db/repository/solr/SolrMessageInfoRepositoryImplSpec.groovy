package dvoraka.avservice.db.repository.solr

import dvoraka.avservice.db.model.MessageInfoDocument
import org.springframework.data.solr.core.SolrTemplate
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Custom repo test.
 */
class SolrMessageInfoRepositoryImplSpec extends Specification {

    SolrMessageInfoRepositoryImpl messageInfoRepository
    SolrTemplate solrTemplate


    def setup() {
        solrTemplate = Mock()
        messageInfoRepository = new SolrMessageInfoRepositoryImpl(solrTemplate)
    }

    @Ignore('WIP')
    def "saveSoft interactions"() {
        given:
            MessageInfoDocument document = new MessageInfoDocument()

        when:
            messageInfoRepository.saveSoft(document)

        then:
            1 * solrTemplate.saveBean(document)
            1 * solrTemplate.softCommit()
    }
}
