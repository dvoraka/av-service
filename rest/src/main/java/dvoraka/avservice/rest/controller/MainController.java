package dvoraka.avservice.rest.controller;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.common.data.MessageStatus;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.common.helper.UuidHelper;
import dvoraka.avservice.rest.service.RestService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;

import static java.util.Objects.requireNonNull;

/**
 * AV REST main controller.
 */
@RestController
public class MainController implements UuidHelper {

    private final RestService avRestService;

    private static final Logger log = LogManager.getLogger(MainController.class);


    @Autowired
    public MainController(RestService restService) {
        this.avRestService = requireNonNull(restService);
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
        return new DefaultAvMessage.Builder(genUuidStr())
                .virusInfo("NO-INFO")
                .correlationId("CORRELATION-ID")
                .data(new byte[dataSize])
                .type(MessageType.RESPONSE)
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
