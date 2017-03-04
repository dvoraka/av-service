package dvoraka.avservice.common.service;

import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Executor service helper.
 */
public interface ExecutorServiceHelper {

    default void shutdownAndAwaitTermination(
            ExecutorService executorService,
            long waitSeconds,
            Logger logger
    ) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(waitSeconds, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(waitSeconds, TimeUnit.SECONDS)) {
                    logger.warn("Thread pool termination problem!");
                }
            } else {
                logger.debug("Thread pool stopping done.");
            }
        } catch (InterruptedException e) {
            logger.warn("Stopping interrupted!", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
