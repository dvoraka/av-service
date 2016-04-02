package dvoraka.avservice.rest;

import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.common.data.MessageStatus;

/**
 * Base for the REST strategy.
 */
public interface RestStrategy {

    MessageStatus messageStatus(String id);

    MessageStatus messageStatus(String id, String serviceId);

    String messageServiceId(String id);

    void messageCheck(AVMessage message);

    AVMessage getResponse(String id);

    void start();

    void stop();
}
