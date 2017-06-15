package dvoraka.avservice.db.service

import dvoraka.avservice.db.configuration.SolrConfig
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore

/**
 * Solr service spec.
 */
@ContextConfiguration(classes = [SolrConfig.class])
@ActiveProfiles(['db-solr'])
@Ignore("Data Solr is probably broken since Kay-M3")
class SolrMessageInfoServiceISpec extends MessageInfoServiceISpec {
}
