package dvoraka.avservice.stats;

import dvoraka.avservice.common.data.AvMessageInfo;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Interface for working with message statistics.
 */
public interface Messages {

    Stream<AvMessageInfo> when(Instant from, Instant to);
}
