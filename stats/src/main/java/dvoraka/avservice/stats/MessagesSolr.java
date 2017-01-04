package dvoraka.avservice.stats;

import dvoraka.avservice.common.data.AvMessageInfo;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Solr messages implementation.
 */
@Service
public class MessagesSolr implements Messages {

    private final MessageInfoService messageService;


    @Autowired
    public MessagesSolr(MessageInfoService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Stream<AvMessageInfo> when(Instant from, Instant to) {
        return messageService.loadInfoStream(from, to);
    }
}
