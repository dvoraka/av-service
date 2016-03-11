package dvoraka.avservice;

import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.AVMessageType;
import dvoraka.avservice.data.DefaultAVMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring REST prototype.
 */
@RestController
public class AVController {

    @RequestMapping(value = "/")
    public String info(@RequestParam(value = "info", defaultValue = "AV service") String info) {

        return info;
    }

    /**
     * Generates testing AV message.
     *
     * @return the generated message
     */
    @RequestMapping(value = "/genmsg")
    public AVMessage generateMessage() {

        return new DefaultAVMessage.Builder(null)
                .serviceId("test-service")
                .type(AVMessageType.RESPONSE)
                .build();
    }
}
