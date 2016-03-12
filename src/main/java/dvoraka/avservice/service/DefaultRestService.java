package dvoraka.avservice.service;

import dvoraka.avservice.data.MessageStatus;
import dvoraka.avservice.rest.RestStrategy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Default REST service implementation.
 */
public class DefaultRestService implements RestService {

    @Autowired
    private RestStrategy restStrategy;

    @Override
    public MessageStatus messageStatus(String id) {
        return restStrategy.messageStatus(id);
    }
}
