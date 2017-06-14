package dvoraka.avservice.db.service

import dvoraka.avservice.db.configuration.DatabaseConfig
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

/**
 * Solr service spec.
 */
@ContextConfiguration(classes = [DatabaseConfig.class])
@ActiveProfiles(['db-solr'])
class SolrMessageInfoServiceISpec extends MessageInfoServiceISpec {
}
