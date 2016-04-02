package dvoraka.avservice.rest;

import dvoraka.avservice.common.data.AVMessage;
import dvoraka.avservice.common.data.DefaultAVMessage;
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

    public void postMessage(AVMessage message, String service) {

        restTemplate.postForObject(baseUrl + service, message, DefaultAVMessage.class);
    }

    public MessageStatus getMessageStatus(String service) {

        return restTemplate.getForObject(baseUrl + service, MessageStatus.class);
    }

    public AVMessage getMessage(String service) {

        return restTemplate.getForObject(baseUrl + service, DefaultAVMessage.class);
    }
}
