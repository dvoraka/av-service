package dvoraka.avservice.service;

import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.MessageStatus;
import dvoraka.avservice.rest.RestStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

/**
 * Default REST service implementation.
 */
public class DefaultRestService implements RestService {

    @Autowired
    private RestStrategy restStrategy;

    private Set<String> processingMessages;


    public DefaultRestService() {
        processingMessages = new HashSet<>();
    }

    @Override
    public MessageStatus messageStatus(String id) {
        return messageStatus(id, null);
    }

    @Override
    public MessageStatus messageStatus(String id, String serviceId) {

        MessageStatus status = restStrategy.messageStatus(id, serviceId);

        if (status == MessageStatus.UNKNOWN && processingMessages.contains(id)) {
            return MessageStatus.WAITING;
        } else {
            return status;
        }
    }

    @Override
    public String messageServiceId(String id) {
        return restStrategy.messageServiceId(id);
    }

    @Override
    public void messageCheck(AVMessage message) {
        processingMessages.add(message.getId());
        restStrategy.messageCheck(message);
    }
}
