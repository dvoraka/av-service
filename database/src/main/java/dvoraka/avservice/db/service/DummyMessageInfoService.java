package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageInfo;
import dvoraka.avservice.common.data.InfoSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Dummy message info service implementation.
 */
@Service
public class DummyMessageInfoService implements MessageInfoService {

    @Override
    public void save(AvMessage message, InfoSource source, String serviceId) {
        // do nothing
    }

    @Override
    public AvMessageInfo loadInfo(String uuid) {
        return null;
    }

    @Override
    public Stream<AvMessageInfo> loadInfoStream(Instant from, Instant to) {
        return Stream.empty();
    }
}
