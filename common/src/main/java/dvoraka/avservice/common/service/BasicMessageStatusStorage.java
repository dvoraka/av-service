package dvoraka.avservice.common.service;

import dvoraka.avservice.common.data.MessageStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Basic implementation for a storage.
 */
public class BasicMessageStatusStorage implements MessageStatusStorage {

    private static final Logger log = LogManager.getLogger(BasicMessageStatusStorage.class);

    private final TimedStorage<String> processingMessages;
    private final TimedStorage<String> processedMessages;


    public BasicMessageStatusStorage(long cacheTimeout) {
        this.processingMessages = new TimedStorage<>(cacheTimeout);
        this.processedMessages = new TimedStorage<>(cacheTimeout);
    }

    @Override
    public void started(String id) {
        processingMessages.put(id);
    }

    @Override
    public void processed(String id) {
        processedMessages.put(id);
        processingMessages.remove(id);
    }

    @Override
    public MessageStatus getStatus(String id) {
        if (processedMessages.contains(id)) {
            return MessageStatus.PROCESSED;
        } else if (processingMessages.contains(id)) {
            return MessageStatus.PROCESSING;
        } else {
            return MessageStatus.UNKNOWN;
        }
    }

    @Override
    public void stop() {
        log.info("Stopping storage...");
        processingMessages.stop();
        processedMessages.stop();
        log.info("Storage stopped.");
    }
}
