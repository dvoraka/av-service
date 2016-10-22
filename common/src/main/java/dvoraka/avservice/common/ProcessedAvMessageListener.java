package dvoraka.avservice.common;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Processed AV message listener.
 */
@FunctionalInterface
public interface ProcessedAvMessageListener {

    /**
     * Receives processed AV messages.
     *
     * @param message the processed message
     */
    void onProcessedAvMessage(AvMessage message);
}
