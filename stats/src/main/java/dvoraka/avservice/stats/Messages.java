package dvoraka.avservice.stats;

import dvoraka.avservice.db.model.MessageInfo;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Interface for working with message statistics.
 */
public interface Messages {

    Stream<MessageInfo> when(Instant from, Instant to);
}
