package dvoraka.avservice.rest.controller;

import dvoraka.avservice.stats.StatsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dvoraka.avservice.rest.controller.StatsController.MAPPING;
import static java.util.Objects.requireNonNull;

/**
 * REST controller for AV statistics.
 */
@RestController
@RequestMapping(MAPPING)
public class StatsController {

    private final StatsService statsService;

    private static final Logger log = LogManager.getLogger(StatsController.class);

    public static final String MAPPING = "/stats";


    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = requireNonNull(statsService);
    }

    @GetMapping("/about")
    public String about() {
        log.info("About called.");
        return "AV statistics";
    }

    @GetMapping("/today")
    public long today() {
        return statsService.todayCount();
    }
}
