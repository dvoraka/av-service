package dvoraka.avservice.rest.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for AV statistics.
 */
@RestController
@RequestMapping("/stats")
public class AvStatsController {

    public static final Logger log = LogManager.getLogger(AvStatsController.class);


    @RequestMapping(value = "/about")
    public String about() {
        return "AV statistics";
    }
}
