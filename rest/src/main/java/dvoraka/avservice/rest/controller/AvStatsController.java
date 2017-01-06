package dvoraka.avservice.rest.controller;

import dvoraka.avservice.stats.StatsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.requireNonNull;

/**
 * REST controller for AV statistics.
 */
@RestController
@RequestMapping("/stats")
public class AvStatsController {

    public static final Logger log = LogManager.getLogger(AvStatsController.class);

    private final StatsService statsService;


    @Autowired
    public AvStatsController(StatsService statsService) {
        this.statsService = requireNonNull(statsService);
    }

    @RequestMapping(value = "/about")
    public String about() {
        return "AV statistics";
    }

    @GetMapping(value = "/today")
    public long today() {
        return statsService.todayCount();
    }
}
