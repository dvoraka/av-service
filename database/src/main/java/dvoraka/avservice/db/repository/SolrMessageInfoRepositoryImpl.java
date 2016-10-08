package dvoraka.avservice.db.repository;

import dvoraka.avservice.db.model.MessageInfoDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.stereotype.Repository;

/**
 * Custom repo implementation.
 */
@Repository
public class SolrMessageInfoRepositoryImpl implements SolrMessageInfoRepositoryCustom {

    @Autowired
    private SolrOperations operations;


    @Override
    public MessageInfoDocument saveSoft(MessageInfoDocument document) {
        operations.saveBean(document);
        operations.softCommit();

        return document;
    }
}
