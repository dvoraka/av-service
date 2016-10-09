package dvoraka.avservice.db.repository.solr;

import dvoraka.avservice.db.model.MessageInfoDocument;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Solr message info repository.
 */
@Repository
public interface SolrMessageInfoRepository
        extends SolrCrudRepository<MessageInfoDocument, String>, SolrMessageInfoRepositoryCustom {
}
