package dvoraka.avservice.storage;

import dvoraka.avservice.common.data.FileMessage;
import dvoraka.avservice.common.data.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    @Before("execution(public void dvoraka.avservice.storage..loadFile(..)) && args(message)")
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
}
