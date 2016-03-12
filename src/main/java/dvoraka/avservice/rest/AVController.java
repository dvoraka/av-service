package dvoraka.avservice.rest;

import dvoraka.avservice.data.MessageStatus;
import dvoraka.avservice.data.AVMessage;
import dvoraka.avservice.data.AVMessageType;
import dvoraka.avservice.data.DefaultAVMessage;
import dvoraka.avservice.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring REST prototype.
 */
@RestController
public class AVController {

    @Autowired
    private RestService restService;

    @RequestMapping(value = "/")
    public String info(@RequestParam(value = "info", defaultValue = "AV service") String info) {

        return info;
    }

    /**
     * Returns the message status.
     *
     * @param id the message ID
     * @return the status
     */
    @RequestMapping(value = "/msg-status/{id}", method = RequestMethod.GET)
    public MessageStatus messageStatus(@PathVariable String id) {
        return restService.messageStatus(id);
    }

    @RequestMapping(value = "/msg-status/{id}/{serviceId}", method = RequestMethod.GET)
    public MessageStatus messageStatus(@PathVariable String id, @PathVariable String serviceId) {
        return restService.messageStatus(id);
    }

    @RequestMapping(value = "/msg-service-id/{id}", method = RequestMethod.GET)
    public String messageServiceId(@PathVariable String id) {
        return restService.messageServiceId(id);
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
