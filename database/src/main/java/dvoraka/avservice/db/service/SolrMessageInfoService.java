package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageInfo;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.model.MessageInfoDocument;
import dvoraka.avservice.db.repository.solr.SolrMessageInfoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Solr AV message info service implementation.
 */
@Service
public class SolrMessageInfoService implements MessageInfoService {

    private static final Logger log = LogManager.getLogger(SolrMessageInfoService.class);

    private static final int BATCH_SIZE = 199;

    private final SolrMessageInfoRepository messageInfoRepository;

    // batching
    private volatile boolean batching;
    private Collection<MessageInfoDocument> documents = new ArrayList<>();
    private int batchSize = BATCH_SIZE;
    //    private long commitEveryMs = 10_000L;
//    private long lastCommitTime;


    @Autowired
    public SolrMessageInfoService(SolrMessageInfoRepository messageInfoRepository) {
        this.messageInfoRepository = requireNonNull(messageInfoRepository);
    }

    @PreDestroy
    public void stop() {
        if (isBatching()) {
            flushCache();
        }
    }

    private void flushCache() {
        if (documents.size() > 0) {
            messageInfoRepository.save(documents);
            documents.clear();
        }
    }

    @Override
    public void save(AvMessage message, AvMessageSource source, String serviceId) {
        log.debug("Saving: " + message.getId());

        MessageInfoDocument messageInfoDocument =
                toMessageInfoDocument(message, source, serviceId);

        if (isBatching()) {
            save(messageInfoDocument);
        } else {
            messageInfoRepository.save(messageInfoDocument);
        }
    }

    /**
     * Save a document in a batch and then save it later at once.
     *
     * @param document the document to save in the batch
     */
    private synchronized void save(MessageInfoDocument document) {
        documents.add(document);

        if (documents.size() >= batchSize) {
            flushCache();
        }
    }

    @Override
    public AvMessageInfo loadInfo(String uuid) {
        MessageInfoDocument document = messageInfoRepository.findByUuid(uuid);

        return document.avMessageInfo();
    }

    @Override
    public Stream<AvMessageInfo> loadInfoStream(Instant from, Instant to) {
        List<MessageInfoDocument> infoDocuments = messageInfoRepository.findByCreatedBetween(
                Date.from(from),
                Date.from(to));

        return infoDocuments.stream()
                .map(MessageInfoDocument::avMessageInfo);
    }

    private MessageInfoDocument toMessageInfoDocument(
            AvMessage message,
            AvMessageSource source,
            String serviceId
    ) {
        MessageInfoDocument messageInfoDocument = new MessageInfoDocument();
        messageInfoDocument.setId(UUID.randomUUID().toString());
        messageInfoDocument.setUuid(message.getId());
        messageInfoDocument.setSource(source.toString());
        messageInfoDocument.setServiceId(serviceId);
        messageInfoDocument.setCreated(new Date());

        return messageInfoDocument;
    }

    public boolean isBatching() {
        return batching;
    }

    public void enableBatching() {
        batching = true;
    }

    public void disableBatching() {
        flushCache();
        batching = false;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
