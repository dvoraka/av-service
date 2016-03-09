package dvoraka.avservice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring REST prototype
 */
@RestController
public class AVController {

    @RequestMapping("/")
    public String info(@RequestParam(value = "info", defaultValue = "AV service") String info) {

        return info;
    }
}
