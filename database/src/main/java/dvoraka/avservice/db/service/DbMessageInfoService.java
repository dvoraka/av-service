package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageInfo;
import dvoraka.avservice.common.data.AvMessageInfoData;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.model.MessageInfo;
import dvoraka.avservice.db.repository.db.DbMessageInfoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * DB message info service implementation.
 */
@Service
@Transactional
public class DbMessageInfoService implements MessageInfoService {

    private static final Logger log = LogManager.getLogger(DbMessageInfoService.class);

    private final DbMessageInfoRepository messageInfoRepository;


    @Autowired
    public DbMessageInfoService(DbMessageInfoRepository messageInfoRepository) {
        this.messageInfoRepository = requireNonNull(messageInfoRepository);
    }

    @Override
    public void save(AvMessage message, AvMessageSource source, String serviceId) {
        log.debug("Saving: " + message.getId());

        MessageInfo messageInfo = toMessageInfo(message, source, serviceId);
        messageInfoRepository.save(messageInfo);
    }

    private MessageInfo toMessageInfo(
            AvMessage message,
            AvMessageSource source,
            String serviceId
    ) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setUuid(message.getId());
        messageInfo.setSource(source.toString());
        messageInfo.setServiceId(serviceId);
        messageInfo.setCreated(new Date());

        return messageInfo;
    }

    @Override
    public AvMessageInfo loadInfo(String uuid) {
        MessageInfo info = messageInfoRepository.findByUuid(uuid);

        return info.avMessageInfo();
    }

    @Override
    public Stream<AvMessageInfo> loadInfoStream(Instant from, Instant to) {
        List<? extends AvMessageInfoData> messageInfos =
                messageInfoRepository.findByCreatedBetween(
                        Date.from(from),
                        Date.from(to));

        return messageInfos.stream()
                .map(AvMessageInfoData::avMessageInfo);
    }
}
