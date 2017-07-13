package dvoraka.avservice.db.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageInfo;
import dvoraka.avservice.common.data.InfoSource;

import java.time.Instant;
import java.util.stream.Stream;

/**
 * Message info service.
 */
public interface MessageInfoService {

    /**
     * Saves an AV message info for a given source and service ID.
     *
     * @param message   the AV message
     * @param source    the source of the AV message
     * @param serviceId the service ID
     */
    void save(AvMessage message, InfoSource source, String serviceId);

    /**
     * Loads a stored AV message info for a given UUID string.
     *
     * @param uuid the AV message UUID string
     * @return the AV message info
     */
    AvMessageInfo loadInfo(String uuid);

    /**
     * Loads a stream of a message info for a given time window.
     *
     * @param from the start time
     * @param to   the end time
     * @return the stream of the message info
     */
    Stream<AvMessageInfo> loadInfoStream(Instant from, Instant to);
}
