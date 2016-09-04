package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.model.MessageInfo;

/**
 * Message info service.
 */
public interface MessageInfoService {

    void save(AvMessage message, AvMessageSource source);

    MessageInfo getMessageInfo(String uuid);
}
