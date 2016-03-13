package dvoraka.avservice.rest;

import dvoraka.avservice.data.DefaultAVMessage;
import org.springframework.web.client.RestTemplate;

/**
 * Rest client.
 */
public class RestClient {

    public static void main(String[] args) {

        String url = "http://localhost:8080/av-service";
        RestTemplate restTemplate = new RestTemplate();
        DefaultAVMessage message = restTemplate.getForObject(url + "/genmsg.json", DefaultAVMessage.class);
        System.out.println(message);
    }
}
