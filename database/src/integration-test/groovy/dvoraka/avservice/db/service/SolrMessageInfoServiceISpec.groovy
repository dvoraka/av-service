package dvoraka.avservice.db.service

import dvoraka.avservice.db.configuration.SolrConfig
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * Solr service spec.
 */
@ContextConfiguration(classes = [SolrConfig.class])
@ActiveProfiles(['db-solr'])
class SolrMessageInfoServiceISpec extends MessageInfoServiceISpec {
}
