package dvoraka.avservice.rest.controller;

import dvoraka.avservice.rest.service.AvRestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dvoraka.avservice.rest.controller.FileController.MAPPING;

/**
 * File controller.
 */
@RestController
@RequestMapping(MAPPING)
public class FileController {

    private final AvRestService restService;

    private static final Logger log = LogManager.getLogger(FileController.class);

    public static final String MAPPING = "/file";


    @Autowired
    public FileController(AvRestService restService) {
        this.restService = restService;
    }
}
