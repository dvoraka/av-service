package dvoraka.avservice.db.repository.solr;

import dvoraka.avservice.db.model.MessageInfoDocument;

/**
 * Solr custom message info repository.
 */
@FunctionalInterface
public interface SolrMessageInfoRepositoryCustom {

    /**
     * Saves a document with a soft commit.
     *
     * @param document the document to save
     */
    void saveSoft(MessageInfoDocument document);
}
