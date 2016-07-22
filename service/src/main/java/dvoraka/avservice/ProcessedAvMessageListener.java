package dvoraka.avservice;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Processed AV message listener.
 */
@FunctionalInterface
public interface ProcessedAvMessageListener {

    void onProcessedAvMessage(AvMessage message);
}
