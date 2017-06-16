package dvoraka.avservice.common.service;

import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Executor service helper.
 */
public interface ExecutorServiceHelper {

    /**
     * Executor service helper method for shutting down an executor service with
     * a given maximum wait time.
     *
     * @param executorService the executor service
     * @param waitSeconds     the max wait time
     * @param logger          the logger
     */
    default void shutdownAndAwaitTermination(
            ExecutorService executorService,
            long waitSeconds,
            Logger logger
    ) {
        logger.info("Stopping executor service...");
        final String okMessage = "Executor service stopping done.";

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(waitSeconds, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(waitSeconds, TimeUnit.SECONDS)) {
                    logger.warn("Thread pool termination problem!");
                } else {
                    logger.info(okMessage);
                }
            } else {
                logger.info(okMessage);
            }
        } catch (InterruptedException e) {
            logger.warn("Stopping interrupted!", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
