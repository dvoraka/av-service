package dvoraka.avservice.rest;

import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.AvMessageSource;
import dvoraka.avservice.db.service.MessageInfoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * REST aspect.
 */
@Aspect
@Component
public class RestAspect {

    private final String serviceId;
    private final MessageInfoService messageInfoService;

    private static final Logger log = LogManager.getLogger(RestAspect.class);


    @Autowired
    public RestAspect(String serviceId, MessageInfoService messageInfoService) {
        this.serviceId = serviceId;
        this.messageInfoService = messageInfoService;
    }

    @Before("execution(public * dvoraka.avservice.rest.controller.*.*(..)) && args(message,..)")
    public void logMessageInfo(AvMessage message) {
        log.debug("Saving message info: {}", message);
        messageInfoService.save(message, AvMessageSource.REST_ENDPOINT, serviceId);
    }
}
