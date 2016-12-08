package dvoraka.avservice.rest;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * Rest client.
 */
public class RestClient {

    @Autowired
    private RestTemplate restTemplate;

    private String baseUrl;


    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
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
