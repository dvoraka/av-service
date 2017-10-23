package dvoraka.avservice.db.repository.solr;

import dvoraka.avservice.db.model.MessageInfoDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * Custom repo implementation.
 */
@Repository
public class SolrMessageInfoRepositoryImpl implements SolrMessageInfoRepositoryCustom {

    private final SolrTemplate solrTemplate;

    private final String collection;


    @Autowired
    public SolrMessageInfoRepositoryImpl(SolrTemplate solrTemplate) {
        this.solrTemplate = Objects.requireNonNull(solrTemplate);
        collection = MessageInfoDocument.class.getAnnotation(SolrDocument.class).collection();
    }

    @Override
    public void saveSoft(MessageInfoDocument document) {
        solrTemplate.saveBean(collection, document);
        solrTemplate.softCommit(collection);
    }
}
