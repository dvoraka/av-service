package dvoraka.avservice.rest;

import dvoraka.avservice.data.AVMessageType;
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
                .serviceId("SERVICE1")
                .virusInfo("UNKNOWN")
                .correlationId("1-2-3")
                .data(new byte[20])
                .type(AVMessageType.REQUEST)
                .build();
        System.out.println("Sent msg: " + msgToUpload);
        restTemplate.postForObject(url + "/msg-check", msgToUpload, DefaultAVMessage.class);
    }
}
