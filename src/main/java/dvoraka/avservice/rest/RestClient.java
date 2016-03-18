package dvoraka.avservice.rest;

import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.DefaultAVMessage;
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

    public AVMessage getTestMessage(String service) {

        return restTemplate.getForObject(baseUrl + service, DefaultAVMessage.class);
    }
}
