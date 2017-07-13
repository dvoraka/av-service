package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageInfo;
import dvoraka.avservice.common.data.AvMessageInfoData;
import dvoraka.avservice.common.data.InfoSource;
import dvoraka.avservice.common.service.ExecutorServiceHelper;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Solr message info service implementation.
 */
@Service
public class SolrMessageInfoService implements MessageInfoService, ExecutorServiceHelper {

    private final SolrMessageInfoRepository messageInfoRepository;

    private static final Logger log = LogManager.getLogger(SolrMessageInfoService.class);

    private static final int BATCH_SIZE = 199;
    public static final long COMMIT_MAX_TIME = 10_000L;

    // batching
    private volatile boolean batching;
    private Collection<MessageInfoDocument> documents = new ArrayList<>();
    private int batchSize = BATCH_SIZE;
    private long commitEveryMs = COMMIT_MAX_TIME;
    private final ScheduledExecutorService executorService;


    @Autowired
    public SolrMessageInfoService(SolrMessageInfoRepository messageInfoRepository) {
        this.messageInfoRepository = requireNonNull(messageInfoRepository);

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(
                this::flushCache,
                commitEveryMs,
                commitEveryMs,
                TimeUnit.MILLISECONDS
        );
    }

    @PreDestroy
    public void stop() {
        if (isBatching()) {
            flushCache();
        }

        final long waitTime = 10;
        shutdownAndAwaitTermination(executorService, waitTime, log);
    }

    private synchronized void flushCache() {
        if (!documents.isEmpty()) {
            messageInfoRepository.save(documents);
            documents.clear();
        }
    }

    @Override
    public void save(AvMessage message, InfoSource source, String serviceId) {
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
     * Saves a document in a batch and then saves it later at once.
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
        List<? extends AvMessageInfoData> infoDocuments =
                messageInfoRepository.findByCreatedBetween(
                        Date.from(from),
                        Date.from(to));

        return infoDocuments.stream()
                .map(AvMessageInfoData::avMessageInfo);
    }

    private MessageInfoDocument toMessageInfoDocument(
            AvMessage message,
            InfoSource source,
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
