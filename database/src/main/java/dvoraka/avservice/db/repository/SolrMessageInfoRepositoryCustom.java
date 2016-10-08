package dvoraka.avservice.db.repository;

import dvoraka.avservice.db.model.MessageInfoDocument;

/**
 * Custom repo.
 */
public interface SolrMessageInfoRepositoryCustom {
    <S extends MessageInfoDocument> S save(S document);
}
