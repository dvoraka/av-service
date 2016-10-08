package dvoraka.avservice.db.repository;

import dvoraka.avservice.db.model.MessageInfoDocument;

/**
 * Custom repo impl.
 */
public class SolrMessageInfoRepositoryCustomImpl implements SolrMessageInfoRepositoryCustom {

    @Override
    public <S extends MessageInfoDocument> S save(S document) {
        System.out.println("SAVE");
        return null;
    }
}
