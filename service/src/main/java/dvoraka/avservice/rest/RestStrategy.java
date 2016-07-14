package dvoraka.avservice.rest;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;

/**
 * Base for the REST strategy.
 */
public interface RestStrategy {

    MessageStatus messageStatus(String id);

    MessageStatus messageStatus(String id, String serviceId);

    String messageServiceId(String id);

    void messageCheck(AvMessage message);

    AvMessage getResponse(String id);

    void start();

    void stop();
}
