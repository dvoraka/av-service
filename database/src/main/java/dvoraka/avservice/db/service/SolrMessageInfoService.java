package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.model.MessageInfo;
import dvoraka.avservice.db.model.MessageInfoDocument;
import dvoraka.avservice.db.repository.SolrMessageInfoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * Solr message info service implementation.
 */
@Service
public class SolrMessageInfoService implements MessageInfoService {

    private static final Logger log =
            LogManager.getLogger(SolrMessageInfoService.class.getName());

    private final SolrMessageInfoRepository messageInfoRepository;


    @Autowired
    public SolrMessageInfoService(SolrMessageInfoRepository messageInfoRepository) {
        this.messageInfoRepository = messageInfoRepository;
    }

    @Override
    public void save(AvMessage message, AvMessageSource source, String serviceId) {
        log.debug("Saving: " + message.getId());

        MessageInfoDocument messageInfoDocument = new MessageInfoDocument();
        // TODO: ID generation should be more precise
        messageInfoDocument.setId(UUID.randomUUID().toString());
        messageInfoDocument.setUuid(message.getId());
        messageInfoDocument.setSource(source.toString());
        messageInfoDocument.setServiceId(serviceId);
        messageInfoDocument.setCreated(new Date());

//        messageInfoRepository.save(messageInfoDocument);
    }

    @Override
    public MessageInfo getMessageInfo(String uuid) {
        return null;
    }
}
