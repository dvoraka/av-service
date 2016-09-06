package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.model.MessageInfo;
import org.springframework.stereotype.Service;

/**
 * Dummy service implementation.
 */
@Service
public class DummyMessageInfoService implements MessageInfoService {

    @Override
    public void save(AvMessage message, AvMessageSource source, String serviceId) {
    }

    @Override
    public MessageInfo getMessageInfo(String uuid) {
        return null;
    }
}
