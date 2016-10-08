package dvoraka.avservice.db.configuration

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


    def "ping Solr"() {
        expect:
            solrClient.ping()
    }
}
