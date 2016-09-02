package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.db.model.MessageInfo;
import dvoraka.avservice.db.repository.MessageInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Default message info service implementation.
 */
public class DefaultMessageInfoService implements MessageInfoService {

    private final MessageInfoRepository messageInfoRepository;


    @Autowired
    public DefaultMessageInfoService(MessageInfoRepository messageInfoRepository) {
        this.messageInfoRepository = messageInfoRepository;
    }

    @Override
    public void save(AvMessage message, String source) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setUuid(message.getId());
        messageInfo.setSource(source);
        messageInfo.setCreated(new Date());

        messageInfoRepository.save(messageInfo);
    }

    @Override
    public MessageInfo getMessageInfo(String uuid) {
        return null;
    }
}
