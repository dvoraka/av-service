package dvoraka.avservice.service;

import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.common.data.MessageStatus;

/**
 * REST service.
 */
public interface RestService {

    MessageStatus messageStatus(String id);

    MessageStatus messageStatus(String id, String serviceId);

    String messageServiceId(String id);

    void messageCheck(AVMessage message);

    AVMessage getResponse(String id);

    void stop();
}
