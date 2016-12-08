package dvoraka.avservice.rest.service;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * REST service.
 */
@Validated
public interface RestService {

    MessageStatus messageStatus(String id);

    MessageStatus messageStatus(String id, String serviceId);

    String messageServiceId(String id);

    void messageCheck(@Valid AvMessage message);

    AvMessage getResponse(String id);

    void stop();
}
