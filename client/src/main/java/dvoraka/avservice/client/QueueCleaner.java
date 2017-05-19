package dvoraka.avservice.client;

/**
 * Cleaner for full/dirty queues.
 */
public interface QueueCleaner {

    /**
     * Receives all available messages from a given queue and throws them out.
     *
     * @param queueName the queue name
     */
    void clean(String queueName);
}
