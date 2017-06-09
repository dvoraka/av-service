package dvoraka.avservice.storage;

import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageType;
import dvoraka.avservice.storage.exception.FileServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * File service aspects.
 */
@Aspect
@Component
public class FileServiceAspect {

    private static final Logger log = LogManager.getLogger(FileServiceAspect.class);

    private static final String BAD_TYPE = "Bad message type!";


    @Before("execution(public void dvoraka.avservice.storage..saveFile(..)) && args(message)")
    public void checkSaveType(FileMessage message) {
        if (message.getType() != MessageType.FILE_SAVE) {
            log.warn(BAD_TYPE);
            throw new IllegalArgumentException(BAD_TYPE);
        }
    }

    @Before("execution(public * dvoraka.avservice.storage..loadFile(..)) && args(message)")
    public void checkLoadType(FileMessage message) {
        if (message.getType() != MessageType.FILE_LOAD) {
            log.warn(BAD_TYPE);
            throw new IllegalArgumentException(BAD_TYPE);
        }
    }

    @Before("execution(public void dvoraka.avservice.storage..updateFile(..)) && args(message)")
    public void checkUpdateType(FileMessage message) {
        if (message.getType() != MessageType.FILE_UPDATE) {
            log.warn(BAD_TYPE);
            throw new IllegalArgumentException(BAD_TYPE);
        }
    }

    @Before("execution(public void dvoraka.avservice.storage..deleteFile(..)) && args(message)")
    public void checkDeleteType(FileMessage message) {
        if (message.getType() != MessageType.FILE_DELETE) {
            log.warn(BAD_TYPE);
            throw new IllegalArgumentException(BAD_TYPE);
        }
    }

    @Around("execution(public * dvoraka.avservice.storage..*File(..)) && args(message)")
    public Object logInfo(ProceedingJoinPoint pjp, FileMessage message)
            throws FileServiceException {

        long start = System.currentTimeMillis();
        String methodName = pjp.getSignature().getName();

        Object result = null;
        try {
            result = pjp.proceed();
        } catch (FileServiceException e) {
            throw e;
        } catch (Throwable throwable) {
            log.warn("Method failed!", throwable);
        }

        long duration = System.currentTimeMillis() - start;
        log.debug("Method: {}, time: {} ms, result: {}", methodName, duration, result);

        return result;
    }
}
