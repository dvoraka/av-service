package dvoraka.avservice;

import dvoraka.avservice.common.data.AvMessage;

/**
 * Processed AV message listener.
 */
public interface ProcessedAVMessageListener {

    void onProcessedAVMessage(AvMessage message);
}
