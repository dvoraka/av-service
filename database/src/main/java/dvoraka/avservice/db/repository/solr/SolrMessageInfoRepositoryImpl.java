package dvoraka.avservice.db.repository.solr;

import dvoraka.avservice.db.model.MessageInfoDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Repository;

/**
 * Custom repo implementation.
 */
@Repository
public class SolrMessageInfoRepositoryImpl implements SolrMessageInfoRepositoryCustom {

    @Autowired
    private SolrTemplate solrTemplate;


    @Override
    public MessageInfoDocument saveSoft(MessageInfoDocument document) {
        solrTemplate.saveBean(document);
        solrTemplate.softCommit();

        return document;
    }
}
