package dvoraka.avservice.rest.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;

/**
 * REST service.
 */
public interface RestService {

    MessageStatus messageStatus(String id);

    MessageStatus messageStatus(String id, String serviceId);

    String messageServiceId(String id);

    void messageCheck(AvMessage message);

    AvMessage getResponse(String id);

    void stop();
}
