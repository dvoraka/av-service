package dvoraka.avservice.common.service;

import dvoraka.avservice.common.data.MessageStatus;

/**
 * Interface for message status storage.
 */
public interface MessageStatusStorage {

    /**
     * Sets a started status for a given ID.
     *
     * @param id the ID
     */
    void started(String id);

    /**
     * Sets a processed status for a given ID.
     *
     * @param id the ID
     */
    void processed(String id);

    /**
     * Returns a status for a given ID.
     *
     * @param id the ID
     * @return the status
     */
    MessageStatus getStatus(String id);

    /**
     * Stops the storage.
     */
    void stop();
}
