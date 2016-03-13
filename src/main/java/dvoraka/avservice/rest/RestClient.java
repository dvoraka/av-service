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
        DefaultAVMessage message = restTemplate.getForObject(url + "/gen-msg.json", DefaultAVMessage.class);
        System.out.println(message);

        DefaultAVMessage msgToUpload = new DefaultAVMessage.Builder("TESTING-REQUEST")
                .data(new byte[10])
                .serviceId("SERVICE1")
                .build();
        System.out.println("Sent msg: " + msgToUpload);
        restTemplate.postForObject(url + "/msg-check", msgToUpload, DefaultAVMessage.class);
    }
}
