package dvoraka.avservice.db.repository.solr;

import dvoraka.avservice.db.model.MessageInfoDocument;

/**
 * Custom repository.
 */
@FunctionalInterface
public interface SolrMessageInfoRepositoryCustom {

    MessageInfoDocument saveSoft(MessageInfoDocument document);
}
