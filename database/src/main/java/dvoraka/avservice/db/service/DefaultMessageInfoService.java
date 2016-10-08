package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.model.MessageInfo;
import dvoraka.avservice.db.repository.MessageInfoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Default message info service implementation.
 */
@Service
@Transactional
public class DefaultMessageInfoService implements MessageInfoService {

    private static final Logger log =
            LogManager.getLogger(DefaultMessageInfoService.class.getName());

    private final MessageInfoRepository messageInfoRepository;


    @Autowired
    public DefaultMessageInfoService(MessageInfoRepository messageInfoRepository) {
        this.messageInfoRepository = messageInfoRepository;
    }

    @Override
    public void save(AvMessage message, AvMessageSource source, String serviceId) {
        log.debug("Saving: " + message.getId());
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setUuid(message.getId());
        messageInfo.setSource(source.toString());
        messageInfo.setServiceId(serviceId);
        messageInfo.setCreated(new Date());

        messageInfoRepository.save(messageInfo);
    }

    @Override
    public MessageInfo getMessageInfo(String uuid) {
        return null;
    }
}
