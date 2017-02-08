package dvoraka.avservice.rest.controller;

import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.rest.service.AvRestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dvoraka.avservice.rest.controller.CheckController.MAPPING;

/**
 * AV REST check controller.
 */
@RestController
@RequestMapping(MAPPING)
public class CheckController {

    private final AvRestService avRestService;

    private static final Logger log = LogManager.getLogger(CheckController.class);

    public static final String MAPPING = "/check";


    @Autowired
    public CheckController(AvRestService restService) {
        this.avRestService = restService;
    }

    @GetMapping("/about")
    public String about() {
        log.info("About called.");
        return "AV checking";
    }

    /**
     * Checks an AV message for viruses.
     *
     * @param message the message
     * @return the status
     */
    @PostMapping("/")
    public ResponseEntity<Void> checkMessage(@RequestBody DefaultAvMessage message) {
        log.debug("Check: {}", message);
        avRestService.checkMessage(message);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
