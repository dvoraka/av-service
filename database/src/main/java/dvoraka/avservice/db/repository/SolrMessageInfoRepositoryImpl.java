package dvoraka.avservice.db.repository;

import dvoraka.avservice.db.model.MessageInfoDocument;
import org.springframework.stereotype.Repository;

/**
 * Custom repo implementation.
 */
@Repository
public class SolrMessageInfoRepositoryImpl implements SolrMessageInfoRepositoryCustom {

    @Override
    public MessageInfoDocument saveSoft(MessageInfoDocument document) {
        System.out.println("Save soft");

        return null;
    }
}
