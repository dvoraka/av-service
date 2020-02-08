package dvoraka.avservice.rest.controller;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.common.helper.UuidHelper;
import dvoraka.avservice.rest.service.RestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static dvoraka.avservice.rest.controller.FileController.MAPPING;
import static java.util.Objects.requireNonNull;

/**
 * File REST controller.
 */
@RestController
@RequestMapping(MAPPING)
public class FileController implements UuidHelper {

    private final RestService restService;

    private static final Logger log = LogManager.getLogger(FileController.class);

    public static final String MAPPING = "/file";
    public static final long LOAD_MAX_TIME = 5_000L;


    @Autowired
    public FileController(RestService restService) {
        this.restService = requireNonNull(restService);
    }

    @GetMapping("/about")
    public String about() {
        log.info("About called.");
        return "File operations";
    }

    @PostMapping("/")
    public ResponseEntity<Void> saveFile(
            @RequestBody DefaultAvMessage fileMessage, Principal principal) {

        log.debug("Save principal: " + principal);
        if (!principal.getName().equals(fileMessage.getOwner())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        restService.saveFile(fileMessage);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<AvMessage> loadFile(@PathVariable String filename, Principal principal) {
        log.debug("Load file: {}, principal: {}", filename, principal);

        AvMessage fileRequest = new DefaultAvMessage.Builder(genUuidStr())
                .filename(filename)
                .owner(principal.getName())
                .type(MessageType.FILE_LOAD)
                .build();
        restService.loadFile(fileRequest);

        AvMessage response;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < LOAD_MAX_TIME) {
            response = restService.getResponse(fileRequest.getId());

            if (response != null) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            final long sleepTime = 100;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.warn("Loading interrupted!", e);
                Thread.currentThread().interrupt();
                break;
            }
        }

        return new ResponseEntity<>(
                fileRequest.createErrorResponse("Load timed out."), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<Void> updateFile(
            @RequestBody DefaultAvMessage fileMessage, Principal principal) {

        log.debug("Update file: {}, principal: {}", fileMessage, principal);
        if (!principal.getName().equals(fileMessage.getOwner())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        restService.updateFile(fileMessage);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<Void> deleteFile(@PathVariable String filename, Principal principal) {
        log.debug("Delete file: {}, principal: {}", filename, principal);

        AvMessage fileRequest = new DefaultAvMessage.Builder(genUuidStr())
                .filename(filename)
                .owner(principal.getName())
                .type(MessageType.FILE_DELETE)
                .build();

        restService.deleteFile(fileRequest);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
