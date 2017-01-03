package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.model.MessageInfo;
import dvoraka.avservice.db.model.MessageInfoDocument;
import dvoraka.avservice.db.repository.solr.SolrMessageInfoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Solr message info service implementation.
 */
@Service
public class SolrMessageInfoService implements MessageInfoService {

    private static final Logger log =
            LogManager.getLogger(SolrMessageInfoService.class.getName());

    private static final int DEFAULT_BATCH_SIZE = 199;

    private final SolrMessageInfoRepository messageInfoRepository;

    private Collection<MessageInfoDocument> documents = new ArrayList<>();
    private int batchSize = DEFAULT_BATCH_SIZE;
//    private long commitEveryMs = 10_000L;
//    private long lastCommitTime;
    private int docsInCollection;


    @Autowired
    public SolrMessageInfoService(SolrMessageInfoRepository messageInfoRepository) {
        this.messageInfoRepository = messageInfoRepository;
    }

    @Override
    public void save(AvMessage message, AvMessageSource source, String serviceId) {
        log.debug("Saving: " + message.getId());

        MessageInfoDocument messageInfoDocument = new MessageInfoDocument();
        messageInfoDocument.setId(UUID.randomUUID().toString());
        messageInfoDocument.setUuid(message.getId());
        messageInfoDocument.setSource(source.toString());
        messageInfoDocument.setServiceId(serviceId);
        messageInfoDocument.setCreated(new Date());

        save(messageInfoDocument);
    }

    private synchronized void save(MessageInfoDocument document) {
        if (docsInCollection == batchSize) {

            messageInfoRepository.save(documents);
            documents.clear();
            resetDocumentCounter();

        } else {
            documents.add(document);
            docsInCollection++;
        }
    }

    private void resetDocumentCounter() {
        docsInCollection = 0;
    }

    @Override
    public MessageInfo loadInfo(String uuid) {
        return null;
    }

    @Override
    public Stream<MessageInfo> loadInfoStream(Instant from, Instant to) {

        List<MessageInfoDocument> infoDocuments = messageInfoRepository.findByCreatedBetween(
                Date.from(from),
                Date.from(to));

        return infoDocuments.stream()
                .map(this::toMessageInfo)
                .collect(Collectors.toList())
                .stream();
    }

    private MessageInfo toMessageInfo(MessageInfoDocument document) {
        return null;
    }

    private MessageInfoDocument fromMessageInfo(MessageInfo info) {
        return null;
    }
}
