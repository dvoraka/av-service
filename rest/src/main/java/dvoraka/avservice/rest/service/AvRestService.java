package dvoraka.avservice.rest.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * Anti-virus REST service.
 */
@Validated
public interface AvRestService {

    MessageStatus messageStatus(String id);

    MessageStatus messageStatus(String id, String serviceId);

    String messageServiceId(String id);

    void messageCheck(@Valid AvMessage message);

    void messageSave(@Valid AvMessage message);

    AvMessage messageLoad(String filename, String ownerId);

    AvMessage getResponse(String id);

    void stop();
}
