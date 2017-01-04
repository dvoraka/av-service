package dvoraka.avservice.stats;

import dvoraka.avservice.common.data.AvMessageInfo;
import dvoraka.avservice.db.service.MessageInfoService;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Solr implementation for messages.
 */
public class MessagesSolr implements Messages {

    private final MessageInfoService messageService;


    public MessagesSolr(MessageInfoService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Stream<AvMessageInfo> when(Instant from, Instant to) {
        return messageService.loadInfoStream(from, to);
    }
}
