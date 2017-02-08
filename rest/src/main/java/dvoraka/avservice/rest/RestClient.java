package dvoraka.avservice.rest;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static java.util.Objects.requireNonNull;

/**
 * Rest client.
 */
@Component
public class RestClient {

    private final RestTemplate restTemplate;

    private final String baseUrl;


    @Autowired
    public RestClient(String baseUrl, RestTemplate restTemplate) {
        this.baseUrl = baseUrl;
        this.restTemplate = requireNonNull(restTemplate);
    }

    public AvMessage postMessage(AvMessage message, String service) {
        return restTemplate.postForObject(baseUrl + service, message, DefaultAvMessage.class);
    }

    public MessageStatus getMessageStatus(String service) {
        return restTemplate.getForObject(baseUrl + service, MessageStatus.class);
    }

    public AvMessage getMessage(String service) {
        return restTemplate.getForObject(baseUrl + service, DefaultAvMessage.class);
    }
}
