package dvoraka.avservice.common.data;

import java.time.Instant;

/**
 * Message info data structure.
 * <br>
 * <br>
 * Data:
 * <ul>
 * <li>ID - the message UUID</li>
 * <li>source - source of the message</li>
 * <li>service ID - the ID of a service</li>
 * <li>created - when the message was created</li>
 * </ul>
 */
public interface AvMessageInfo {

    String getId();

    InfoSource getSource();

    String getServiceId();

    Instant getCreated();
}
