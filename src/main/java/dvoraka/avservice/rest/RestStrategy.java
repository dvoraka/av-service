package dvoraka.avservice.rest;

import dvoraka.avservice.data.MessageStatus;

/**
 * Base for the REST strategy.
 */
public interface RestStrategy {

    MessageStatus messageStatus(String id);

    MessageStatus messageStatus(String id, String serviceId);

    String messageServiceId(String id);
}
