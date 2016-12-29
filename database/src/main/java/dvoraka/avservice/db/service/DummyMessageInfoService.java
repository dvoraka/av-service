package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.model.MessageInfo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Dummy service implementation.
 */
@Service
public class DummyMessageInfoService implements MessageInfoService {

    @Override
    public void save(AvMessage message, AvMessageSource source, String serviceId) {
        // do nothing
    }

    @Override
    public MessageInfo loadInfo(String uuid) {
        return null;
    }

    @Override
    public Stream<MessageInfo> loadInfoStream(Instant from, Instant to) {
        return Stream.empty();
    }
}
