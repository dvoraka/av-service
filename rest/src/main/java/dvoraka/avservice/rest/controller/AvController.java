package dvoraka.avservice.rest.controller;

import dvoraka.avservice.common.Utils;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageType;
import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.rest.service.AvRestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;

/**
 * AV REST controller.
 */
@RestController
public class AvController {

    private final AvRestService avRestService;

    private static final Logger log = LogManager.getLogger(AvController.class);


    @Autowired
    public AvController(AvRestService restService) {
        this.avRestService = restService;
    }

    /**
     * Testing method.
     *
     * @param info the info String
     * @return the info
     */
    @GetMapping("/")
    public String info(@RequestParam(value = "info", defaultValue = "AV service") String info) {
        return info;
    }

    /**
     * Returns the message status.
     *
     * @param id the message ID
     * @return the status
     */
    @GetMapping("/msg-status/{id}")
    public MessageStatus messageStatus(@PathVariable String id) {
        return avRestService.messageStatus(id);
    }

    /**
     * Returns the message status.
     *
     * @param id        the message ID
     * @param serviceId the service ID
     * @return the status
     */
    @GetMapping("/msg-status/{id}/{serviceId}")
    public MessageStatus messageStatus(
            @PathVariable String id,
            @PathVariable String serviceId
    ) {
        return avRestService.messageStatus(id, serviceId);
    }

    /**
     * Returns a service ID for the message ID.
     *
     * @param id the message ID
     * @return the service ID
     */
    @GetMapping("/msg-service-id/{id}")
    public String messageServiceId(@PathVariable String id) {
        return avRestService.messageServiceId(id);
    }

    /**
     * Checks an AV message for viruses.
     *
     * @param message the message
     * @return the status
     */
    @PostMapping("/msg-check")
    public ResponseEntity<Void> messageCheck(@RequestBody DefaultAvMessage message) {
        log.debug("Check: {}", message);
        avRestService.checkMessage(message);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    /**
     * Returns an info about a message check.
     *
     * @param id the message ID
     * @return the message with info
     */
    @GetMapping("/get-response/{id}")
    public AvMessage getResponse(@PathVariable String id) {
        return avRestService.getResponse(id);
    }

    /**
     * Generates a testing AV message.
     *
     * @return the generated message
     */
    @GetMapping("/gen-msg")
    public AvMessage generateMessage() {
        final int dataSize = 10;
        return new DefaultAvMessage.Builder(Utils.genUuidString())
                .serviceId("REST")
                .virusInfo("NO-INFO")
                .correlationId("CORRELATION-ID")
                .data(new byte[dataSize])
                .type(AvMessageType.RESPONSE)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public AvMessage handleConstraintViolationException(
            ConstraintViolationException exception) {
        log.info("Constraint exception: {}", exception);

        return null;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public AvMessage handleBadMessageException(HttpMessageNotReadableException exception) {
        log.info("Msg not readable: {}", exception);

        return null;
    }
}
