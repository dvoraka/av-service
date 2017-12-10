package dvoraka.avservice.client.util;

/**
 * Cleaner for full/dirty queues.
 */
@FunctionalInterface
public interface QueueCleaner {

    /**
     * Receives all available messages from a given queue/destination and throws them out.
     *
     * @param name the queue/destination name
     */
    void clean(String name);
}
