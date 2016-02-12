package dvoraka.avservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Main AV message processor.
 */
public class DefaultMessageProcessor implements MessageProcessor {

    @Autowired
    private AVService avService;

    private static final Logger log = LogManager.getLogger(SimpleAmqpListeningStrategy.class.getName());


    @Override
    public void sendMessage(AVMessage message) {
        log.debug("Processing message...");
        avService.scanStream(message.getData());
        log.debug("Message processed.");
    }

    @Override
    public boolean hasProcessedMessage() {
        return false;
    }

    @Override
    public AVMessage getProcessedMessage() {
        return null;
    }
}
