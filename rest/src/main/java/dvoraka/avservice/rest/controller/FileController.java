package dvoraka.avservice.rest.controller;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.rest.service.AvRestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static dvoraka.avservice.rest.controller.FileController.MAPPING;

/**
 * File REST controller.
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

    @GetMapping("/about")
    public String about() {
        log.info("About called.");
        return "AV file operations";
    }

    @GetMapping("/load/{filename}")
    public AvMessage loadFile(@PathVariable String filename, Principal principal) {

        AvMessage fileRequest = new DefaultAvMessage.Builder(Utils.genUuidString())
                .filename(filename)
                .owner(principal.getName())
                .type(MessageType.FILE_LOAD)
                .build();

        return restService.loadMessage(fileRequest);
    }

    @PostMapping("/save")
    public ResponseEntity<Void> saveFile(@RequestBody DefaultAvMessage file, Principal principal) {
        String username = principal.getName();
        log.info("Principal: " + principal);

        if (!username.equals(file.getOwner())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            restService.saveMessage(file);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
    }
}
