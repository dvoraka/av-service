package dvoraka.avservice.db.repository.solr;

import dvoraka.avservice.db.model.MessageInfoDocument;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Solr message info repository.
 */
@Repository
public interface SolrMessageInfoRepository extends
        SolrCrudRepository<MessageInfoDocument, String>,
        SolrMessageInfoRepositoryCustom {

    List<MessageInfoDocument> findByCreatedBetween(Date from, Date to);
}
