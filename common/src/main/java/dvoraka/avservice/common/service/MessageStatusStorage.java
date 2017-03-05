package dvoraka.avservice.common.service;

import dvoraka.avservice.common.data.MessageStatus;

/**
 * Interface for message status storage.
 */
public interface MessageStatusStorage {

    void started(String id);

    void processed(String id);

    MessageStatus getStatus(String id);

    void stop();
}
